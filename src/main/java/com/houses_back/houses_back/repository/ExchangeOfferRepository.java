package com.houses_back.houses_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.houses_back.houses_back.model.ExchangeOffer;

public interface ExchangeOfferRepository extends JpaRepository<ExchangeOffer, Long> {
    List<ExchangeOffer> findByChatLoginOrderByCreatedAtDesc(String chatLogin);
}
