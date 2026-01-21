package com.houses_back.houses_back.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_daily_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userLogin;

    @Column(nullable = false)
    private String chatLogin;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int completedTasksCount = 0;
}