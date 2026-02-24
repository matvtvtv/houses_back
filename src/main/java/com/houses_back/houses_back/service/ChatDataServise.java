package com.houses_back.houses_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
  @Transactional
public ChatData joinChat(String chatLogin, String userLogin) {

    // 1️⃣ Проверка существования чата
    if (!chatDataRepository.existsByChatLogin(chatLogin)) {
        throw new RuntimeException("Chat does not exist");
    }

    // 2️⃣ Проверяем, есть ли уже пользователь
    Optional<ChatData> existingUser =
            chatDataRepository.findByChatLoginAndUserLogin(chatLogin, userLogin);

    if (existingUser.isPresent()) {
        return existingUser.get(); // возвращаем ChatData, НЕ Optional
    }

    // 3️⃣ Получаем любую запись чата
    ChatData parentChat = chatDataRepository
            .findByChatLogin(chatLogin)
            .stream()
            .findFirst()
            .orElseThrow();

    // 4️⃣ Создаём нового участника
    ChatData chat = new ChatData();
    chat.setChatLogin(chatLogin);
    chat.setChatName(parentChat.getChatName());
    chat.setUserLogin(userLogin);
    chat.setUserRole("CHILD");
    chat.setMoney(0);

    return chatDataRepository.save(chat); // save(ChatData)
}
    @Transactional
    public void deleteByUserLoginAndChatLogin(String userLogin, String chatLogin) {
        chatDataRepository.deleteByUserLoginAndChatLogin(userLogin, chatLogin);
    }
}
