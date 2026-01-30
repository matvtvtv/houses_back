package com.houses_back.houses_back.controller;

import com.houses_back.houses_back.model.ExchangeOffer;
import com.houses_back.houses_back.service.ExchangeOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeOfferService service;
    private final SimpMessagingTemplate messaging;

    // Создать предложение
    @PostMapping("/create")
    public ExchangeOffer create(@RequestBody ExchangeOffer offer) {
        ExchangeOffer created = service.create(offer);
        // уведомляем подписчиков данного чата
        messaging.convertAndSend("/topic/exchange/" + created.getChatLogin(), created);
        return created;
    }

    // Список по chatLogin
    @GetMapping("/{chatLogin}")
    public List<ExchangeOffer> listByChat(@PathVariable String chatLogin) {
        return service.findByChatLogin(chatLogin);
    }

    @GetMapping("/{chatLogin}/{id}")
    public ExchangeOffer getOne(@PathVariable String chatLogin, @PathVariable Long id){
        return service.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PutMapping("/{id}")
    public ExchangeOffer update(@PathVariable Long id, @RequestBody ExchangeOffer updated) {
        ExchangeOffer u = service.update(id, updated);
        messaging.convertAndSend("/topic/exchange/" + u.getChatLogin(), u);
        return u;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ExchangeOffer ex = service.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        service.delete(id);
        messaging.convertAndSend("/topic/exchange/" + ex.getChatLogin(), ex);
    }
}
