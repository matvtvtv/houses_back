package com.houses_back.houses_back.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDTO  {
    private String userLogin;
    private String chatLogin;
    private int money;
    private int totalCompletedTasks;
    private LocalDate fromDate;
    private LocalDate toDate;
}