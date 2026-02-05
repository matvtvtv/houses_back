package com.houses_back.houses_back.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.houses_back.houses_back.dto.DailyStatsDTO;
import com.houses_back.houses_back.dto.UserStatsDTO;
import com.houses_back.houses_back.model.UserDailyStats;
import com.houses_back.houses_back.repository.UserDailyStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserDailyStatsRepository statsRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Получить статистику пользователя за последние 2 месяца (по умолчанию)
     * или за указанный период
     */
    public UserStatsDTO getUserStats(String chatLogin, String userLogin, LocalDate from, LocalDate to) {
        if (from == null) {
            from = YearMonth.now().minusMonths(1).atDay(1);
        }
        if (to == null) {
            to = LocalDate.now();
        }

        int totalMoney = statsRepository.sumMoneyByPeriod(userLogin, chatLogin, from, to);
        int totalTasks = statsRepository.sumTasksByPeriod(userLogin, chatLogin, from, to);

        List<UserDailyStats> dailyStats = statsRepository
                .findByUserLoginAndChatLoginAndDateBetweenOrderByDateDesc(userLogin, chatLogin, from, to);

        Map<String, Integer> moneyByMonth = dailyStats.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getDate().format(MONTH_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.summingInt(UserDailyStats::getEarnedMoney)
                ));

        Map<String, Integer> tasksByMonth = dailyStats.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getDate().format(MONTH_FORMATTER),
                        LinkedHashMap::new,
                        Collectors.summingInt(UserDailyStats::getCompletedTasksCount)
                ));

        return UserStatsDTO.builder()
                .userLogin(userLogin)
                .chatLogin(chatLogin)
                .money(totalMoney)
                .totalCompletedTasks(totalTasks)
                .fromDate(from)
                .toDate(to)
                .moneyByMonth(moneyByMonth)
                .tasksByMonth(tasksByMonth)
                .build();
    }

    /**
     * Получить статистику за конкретный месяц
     */
    public int getMoneyForMonth(String chatLogin, String userLogin, YearMonth yearMonth) {
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();
        return statsRepository.sumMoneyByPeriod(userLogin, chatLogin, from, to);
    }

    /**
     * Лидерборд за последние 2 месяца
     */
    public List<UserStatsDTO> getLeaderboard(String chatLogin, String sortBy) {
        LocalDate from = YearMonth.now().minusMonths(1).atDay(1);
        LocalDate to = LocalDate.now();

        List<Object[]> results;

        if ("tasks".equals(sortBy)) {
            results = statsRepository.getLeaderboardByTasks(chatLogin, from, to);
        } else {
            results = statsRepository.getLeaderboardByMoney(chatLogin, from, to);
        }

        return results.stream().map(row -> UserStatsDTO.builder()
                .userLogin((String) row[0])
                .chatLogin(chatLogin)
                .money(((Number) row[1]).intValue())
                .totalCompletedTasks(((Number) row[2]).intValue())
                .fromDate(from)
                .toDate(to)
                .build())
                .collect(Collectors.toList());
    }

    /**
     * Записать выполнение задачи (вызывается при завершении задачи)
     */
    @Transactional
    public void recordTaskCompletion(String userLogin, String chatLogin, int rewardMoney) {
        LocalDate today = LocalDate.now();

        UserDailyStats stats = statsRepository
                .findByUserLoginAndChatLoginAndDate(userLogin, chatLogin, today)
                .orElseGet(() -> UserDailyStats.builder()
                        .userLogin(userLogin)
                        .chatLogin(chatLogin)
                        .date(today)
                        .completedTasksCount(0)
                        .earnedMoney(0)
                        .build());

        stats.setCompletedTasksCount(stats.getCompletedTasksCount() + 1);
        stats.setEarnedMoney(stats.getEarnedMoney() + rewardMoney);

        statsRepository.save(stats);
    }

    /**
     * Метод, который возвращает список дневных DTO для диапазона (используется
     * эндпоинтом /api/stats/daily)
     */
    public List<DailyStatsDTO> getDailyStats(String chatLogin, String userLogin, LocalDate from, LocalDate to) {
        List<UserDailyStats> list = statsRepository
                .findByUserLoginAndChatLoginAndDateBetweenOrderByDateDesc(userLogin, chatLogin, from, to);

        return list.stream()
                .map(u -> DailyStatsDTO.builder()
                        .date(u.getDate())
                        .completedTasksCount(u.getCompletedTasksCount())
                        .build())
                .collect(Collectors.toList());
    }
}
