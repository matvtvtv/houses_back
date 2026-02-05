package com.houses_back.houses_back.service;

import com.houses_back.houses_back.model.ExchangeOffer;
import com.houses_back.houses_back.repository.ExchangeOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeOfferService {
    private final ExchangeOfferRepository repo;

    public ExchangeOffer create(ExchangeOffer e) {
        e.setCreatedAt(Instant.now());
        e.setActive(true);
        return repo.save(e);
    }

    public Optional<ExchangeOffer> findById(Long id) { return repo.findById(id); }

    public List<ExchangeOffer> findByChatLogin(String chatLogin) {
        return repo.findByChatLoginOrderByCreatedAtDesc(chatLogin);
    }

    public ExchangeOffer update(Long id, ExchangeOffer updated) {
    return repo.findById(id).map(existing -> {
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setMonth(updated.getMonth()); // ИЗМЕНЕНО
        existing.setActive(updated.isActive());
        return repo.save(existing);
    }).orElseThrow(() -> new RuntimeException("Not found"));
}

    public void delete(Long id) { repo.deleteById(id); }
}
