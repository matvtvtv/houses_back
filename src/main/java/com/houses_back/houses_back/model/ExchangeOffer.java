package com.houses_back.houses_back.model;

import java.time.Instant;
import java.time.Month; // Добавить импорт

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "exchange_offers")
@Data
public class ExchangeOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatLogin;

    @Column(nullable = false)
    private String ownerLogin;

    // ИЗМЕНЕНО: month вместо cost
    @Column(nullable = false)
    private Month month; // Используем java.time.Month

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    private Instant createdAt = Instant.now();
}