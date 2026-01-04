package com.houses_back.houses_back.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.model.ChatMessage;
import com.houses_back.houses_back.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatMessageRepository repository;

    @GetMapping("/history/{chatId}")
    public List<ChatMessage> history(@PathVariable String chatId) {
        return repository.findByChatIdOrderByTimestampAsc(chatId);
    }
}