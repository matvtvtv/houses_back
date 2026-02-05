package com.houses_back.houses_back.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDTO {
    private String userLogin;
    private String chatLogin;
    
    // За запрошенный период
    private int money;
    private int totalCompletedTasks;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Разбивка по месяцам (ключ: "2024-01", значение: сумма)
    private Map<String, Integer> moneyByMonth;
    private Map<String, Integer> tasksByMonth;
}