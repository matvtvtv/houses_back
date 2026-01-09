package com.houses_back.houses_back.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.houses_back.houses_back.model.ChatMessage;
import com.houses_back.houses_back.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatLogin}/send")
    public void sendMessage(
            @DestinationVariable String chatLogin,
            ChatMessage message
    ) {
        message.setChatLogin(chatLogin);
        repository.save(message);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatLogin,
                message
        );
    }
}
