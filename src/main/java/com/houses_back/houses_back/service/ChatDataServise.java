package com.houses_back.houses_back.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.repository.ChatDataRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor

public class ChatDataServise {

    private final ChatDataRepository chatDataRepository;

    public List<ChatData> findByUserLogin(String userLogin) {
        return chatDataRepository.findByUserLogin(userLogin);
    }

    public List<ChatData> findByChatLogin(String chatLogin) {
        return chatDataRepository.findByChatLogin(chatLogin);
    }

    public ChatData register(String chatLogin, String chatName , String userLogin,String userRole) {

        if (chatDataRepository.existsByChatLoginAndUserLogin(chatLogin, userLogin)) {
            throw new RuntimeException("Already exists");
        }

        ChatData chat = new ChatData();
        chat.setChatLogin(chatLogin);
        chat.setChatName(chatName);
        chat.setUserLogin(userLogin);
        chat.setUserRole(userRole);


        return chatDataRepository.save(chat);
    }
    
    public ChatData joinChat(String chatLogin, String userLogin) {

   
        if (!chatDataRepository.existsByChatLogin(chatLogin)) {
            throw new RuntimeException("Chat does not exist");
        }

        if (chatDataRepository.existsByChatLoginAndUserLogin(chatLogin, userLogin)) {
            throw new RuntimeException("User already in chat");
        }

        ChatData parentChat = chatDataRepository
            .findAll()
            .stream()
            .filter(c -> c.getChatLogin().equals(chatLogin))
            .findFirst()
            .orElseThrow();

        ChatData chat = new ChatData();
        chat.setChatLogin(chatLogin);
        chat.setChatName(parentChat.getChatName());
        chat.setUserLogin(userLogin);
        chat.setUserRole("CHILD");
        chat.setMoney(0);

        return chatDataRepository.save(chat);
    }

}
