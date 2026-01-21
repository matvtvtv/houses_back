package com.houses_back.houses_back.controller;

import java.time.LocalDate;
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
     * Рейтинг всех пользователей в чате (по деньгам или задачам)
     */
    @GetMapping("/{chatLogin}/leaderboard")
    public ResponseEntity<List<UserStatsDTO>> getLeaderboard(
            @PathVariable String chatLogin,
            @RequestParam(defaultValue = "money") String sortBy
    ) {
        return ResponseEntity.ok(statsService.getLeaderboard(chatLogin, sortBy));
    }

    /**
     * Статистика конкретного пользователя за период
     */
    @GetMapping("/{chatLogin}/{userLogin}")
    public ResponseEntity<UserStatsDTO> getUserStats(
            @PathVariable String chatLogin,
            @PathVariable String userLogin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();
        
        return ResponseEntity.ok(statsService.getUserStats(chatLogin, userLogin, from, to));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyStatsDTO>> getDailyStats(
            @RequestParam String chatLogin,
            @RequestParam String userLogin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        // Если даты не указаны - берём последние 14 дней
        if (from == null) from = LocalDate.now().minusDays(13);
        if (to == null) to = LocalDate.now();

        List<DailyStatsDTO> stats = statsService.getDailyStats(chatLogin, userLogin, from, to);
        return ResponseEntity.ok(stats);
    }
}