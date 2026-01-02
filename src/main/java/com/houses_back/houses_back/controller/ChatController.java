package com.houses_back.houses_back.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.houses_back.houses_back.model.ChatMessage;
import com.houses_back.houses_back.repository.ChatMessageRepository;

@Controller
public class ChatController {

    private final ChatMessageRepository repository;

    public ChatController(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @MessageMapping("/send") // соответствует /app/send
    @SendTo("/topic/messages") // куда будут получать все клиенты
    public ChatMessage sendMessage(ChatMessage message) {
        repository.save(message); // сохраняем в БД
        return message;           // возвращаем всем подписчикам
    }
}
