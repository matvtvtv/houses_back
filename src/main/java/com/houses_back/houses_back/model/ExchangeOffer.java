package com.houses_back.houses_back.model;

import java.time.Instant;

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

    // логин чата — к какому чату привязано предложение
    @Column(nullable = false)
    private String chatLogin;

    // кто создал предложение (логин)
    @Column(nullable = false)
    private String ownerLogin;

    // стоимость в монетах
    @Column(nullable = false)
    private Integer cost;

    // краткое название (что за обмен)
    @Column(nullable = false)
    private String title;

    // описание того, что получит пользователь
    @Column(columnDefinition = "TEXT")
    private String description;

    // флаг активности (если предложено, но позже завершено/отключено)
    @Column(nullable = false)
    private boolean active = true;

    private Instant createdAt = Instant.now();
}
