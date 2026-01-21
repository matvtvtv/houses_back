package com.houses_back.houses_back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "task_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userLogin;

    @Column(nullable = false)
    private String chatLogin;

    private String targetLogin;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private int money;

    private LocalDate startDate;

    private boolean repeat = false;

    @ElementCollection
    @CollectionTable(name = "task_template_repeat_days", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "day_name")
    private List<String> repeatDays;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Автоматическая установка createdAt перед сохранением
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Автоматическая установка updatedAt перед обновлением
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
