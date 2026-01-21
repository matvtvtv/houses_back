package com.houses_back.houses_back.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.houses_back.houses_back.dto.DailyStatsDTO;
import com.houses_back.houses_back.dto.UserStatsDTO;
import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.model.UserDailyStats;
import com.houses_back.houses_back.repository.ChatDataRepository;
import com.houses_back.houses_back.repository.UserDailyStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final UserDailyStatsRepository statsRepository;

    private final ChatDataRepository chatDataRepository;

    public UserStatsDTO getUserStats(String chatLogin, String userLogin, LocalDate from, LocalDate to) {
        ChatData chatData = chatDataRepository.findByChatLoginAndUserLogin(chatLogin, userLogin)
                .orElseThrow(() -> new RuntimeException("User not found in chat"));

        List<UserDailyStats> stats = statsRepository.findAllByUserLoginAndChatLoginAndDateBetween(
                userLogin, chatLogin, from, to);

        int totalTasks = stats.stream().mapToInt(UserDailyStats::getCompletedTasksCount).sum();

        return UserStatsDTO.builder()
                .userLogin(userLogin)
                .chatLogin(chatLogin)
                .money(chatData.getMoney())
                .totalCompletedTasks(totalTasks)
                .fromDate(from)
                .toDate(to)
                .build();
    }

    public List<UserStatsDTO> getLeaderboard(String chatLogin, String sortBy) {
        List<ChatData> chatUsers = chatDataRepository.findByChatLogin(chatLogin);
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();

        return chatUsers.stream()
                .map(chatData -> getUserStats(chatLogin, chatData.getUserLogin(), from, to))
                .sorted((a, b) -> {
                    if ("money".equals(sortBy)) {
                        return Integer.compare(b.getMoney(), a.getMoney());
                    } else {
                        return Integer.compare(b.getTotalCompletedTasks(), a.getTotalCompletedTasks());
                    }
                })
                .collect(Collectors.toList());
    }

   public List<DailyStatsDTO> getDailyStats(String chatLogin, String userLogin, LocalDate from, LocalDate to) {
        // Получаем существующие записи из БД
        List<UserDailyStats> existingStats = statsRepository.findAllByUserLoginAndChatLoginAndDateBetween(
                userLogin, chatLogin, from, to);

        // Создаем Map для быстрого поиска по дате
        Map<LocalDate, Integer> statsMap = existingStats.stream()
                .collect(Collectors.toMap(UserDailyStats::getDate, UserDailyStats::getCompletedTasksCount));

        // Генерируем результат со всеми днями в диапазоне
        List<DailyStatsDTO> result = new ArrayList<>();
        LocalDate current = from;
        while (!current.isAfter(to)) {
            result.add(DailyStatsDTO.builder()
                    .date(current)
                    .completedTasksCount(statsMap.getOrDefault(current, 0))
                    .build());
            current = current.plusDays(1);
        }

        return result;
    }
}