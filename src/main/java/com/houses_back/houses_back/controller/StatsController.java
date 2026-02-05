package com.houses_back.houses_back.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.DailyStatsDTO;
import com.houses_back.houses_back.dto.UserStatsDTO;
import com.houses_back.houses_back.service.StatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;

    /**
     * Лидерборд за последние 2 месяца (текущий + прошлый)
     */
    @GetMapping("/{chatLogin}/leaderboard")
    public ResponseEntity<List<UserStatsDTO>> getLeaderboard(
            @PathVariable String chatLogin,
            @RequestParam(defaultValue = "money") String sortBy
    ) {
        return ResponseEntity.ok(statsService.getLeaderboard(chatLogin, sortBy));
    }

    /**
     * Статистика пользователя за последние 2 месяца или указанный период
     */
    @GetMapping("/{chatLogin}/{userLogin}")
    public ResponseEntity<UserStatsDTO> getUserStats(
            @PathVariable String chatLogin,
            @PathVariable String userLogin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(statsService.getUserStats(chatLogin, userLogin, from, to));
    }

    /**
     * Статистика за конкретный месяц (например, "2024-01")
     */
    @GetMapping("/{chatLogin}/{userLogin}/month")
    public ResponseEntity<Integer> getMonthlyMoney(
            @PathVariable String chatLogin,
            @PathVariable String userLogin,
            @RequestParam String yearMonth // формат: "2024-01"
    ) {
        YearMonth ym = YearMonth.parse(yearMonth);
        int money = statsService.getMoneyForMonth(chatLogin, userLogin, ym);
        return ResponseEntity.ok(money);
    }
    @GetMapping("/daily")
public ResponseEntity<List<DailyStatsDTO>> getDaily(
        @RequestParam String chatLogin,
        @RequestParam String userLogin,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
) {
    return ResponseEntity.ok(statsService.getDailyStats(chatLogin, userLogin, from, to));
}
}