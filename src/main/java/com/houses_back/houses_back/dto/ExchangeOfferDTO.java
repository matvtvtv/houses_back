package com.houses_back.houses_back.dto;

import java.time.Month; // Добавить импорт

import lombok.Data;

@Data
public class ExchangeOfferDTO {
    public Long id;
    public String chatLogin;
    public String ownerLogin;
    public Month month; // ИЗМЕНЕНО
    public String title;
    public String description;
    public Boolean active;
    public String createdAt;
}