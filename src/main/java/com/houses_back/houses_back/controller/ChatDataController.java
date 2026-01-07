package com.houses_back.houses_back.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.ChatDataDTO;
import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.service.ChatDataServise;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/chats_data")
@RequiredArgsConstructor
public class ChatDataController {


    private final ChatDataServise chatDataServise;

    @PostMapping("/register")
    public ChatData register(@RequestBody ChatDataDTO request) {
        return chatDataServise.register(
            request.getChatLogin(),
            request.getUserLogin(),
            request.getUserRole()
        );
    }
}
