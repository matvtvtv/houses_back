package com.houses_back.houses_back.dto;

import lombok.Data;

@Data
public class ExchangeOfferDTO {
    public Long id;
    public String chatLogin;
    public String ownerLogin;
    public Integer cost;
    public String title;
    public String description;
    public Boolean active;
    public String createdAt;
}
