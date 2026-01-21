package com.houses_back.houses_back.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кто откликнулся задачу (логин), если null то еще никто не откликнулся
    private String userLogin;

     @Column(nullable = false)
    private String chatLogin;
;

    // Логин того, кому назначена задача (null = всем)
    private String targetLogin;

    // Название задачи (обязательно)
    @Column(nullable = false)
    private String title;

    // Описание (необязательно)
    @Column(columnDefinition = "text")
    private String description;

    // Количество монет (обязательно)
    @Column(nullable = false)
    private int money;

    // Дата начала задачи (необязательная)
    private LocalDate startDate;

    // Список дней повторения (например: ["MONDAY", "WEDNESDAY"])
    @Column(name = "days", columnDefinition = "text[]")
    private String[] days;

    // Флаги состояния
    private boolean repeat = false;       // повторяемость задачи
    private boolean completed = false;    // завершена ли

    // Дата создания
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    
}
