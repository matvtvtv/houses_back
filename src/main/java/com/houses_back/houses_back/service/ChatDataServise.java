package com.houses_back.houses_back.service;

import org.springframework.stereotype.Service;

import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.repository.ChatDataRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor

public class ChatDataServise {

    private final ChatDataRepository chatDataReposytori;



    public ChatData register (String chatLogin, String userLogin, String userRole) {

        if (chatDataReposytori.findByChatLoginAndUserLogin(chatLogin, userLogin).isPresent()) {
            throw new RuntimeException("Login already exists");
        }

        ChatData chat = new ChatData();
        chat.setChatLogin(chatLogin);
        chat.setUserLogin(userLogin);
        chat.setUserRole(userRole);

        return chatDataReposytori.save(chat);

    }
}
