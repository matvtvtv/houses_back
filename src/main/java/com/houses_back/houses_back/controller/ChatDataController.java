package com.houses_back.houses_back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.ChatDataDTO;
import com.houses_back.houses_back.dto.JoinChatDataDTO;
import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.repository.ChatDataRepository;
import com.houses_back.houses_back.service.ChatDataServise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chats_data")
@RequiredArgsConstructor
public class ChatDataController {

    private final ChatDataServise chatDataServise;
    private final ChatDataRepository chatDataRepo;
    
    @PostMapping("/register")
    public ChatData register(@RequestBody ChatDataDTO request) {
        return chatDataServise.register(
            request.getChatLogin(),
            request.getChatName(),
            request.getUserLogin(),
            request.getUserRole()
        );
    }
    
    @PostMapping("/join")
    public ChatData join(@RequestBody JoinChatDataDTO request) {
        return chatDataServise.joinChat(
            request.getChatLogin(),
            request.getUserLogin()
        );
    }
    
    @GetMapping("/get_chats/{userLogin}")
    public List<ChatData> findByUserLogin(@PathVariable String userLogin) {
        return chatDataServise.findByUserLogin(userLogin);
    }
    
    @GetMapping("/get_chats_users/{chatLogin}")
    public List<ChatData> findByChatLogin(@PathVariable String chatLogin) {
        return chatDataServise.findByChatLogin(chatLogin);
    }
    
   @Transactional
@DeleteMapping("/delete")
public ResponseEntity<?> deleteByUserLoginAndChatLogin(
        @RequestParam("userLogin") String userLogin,
        @RequestParam("chatLogin") String chatLogin) {
    
    log.info("Получен запрос на удаление: userLogin={}, chatLogin={}", userLogin, chatLogin);
    
    if (userLogin == null || userLogin.isEmpty() || chatLogin == null || chatLogin.isEmpty()) {
        return ResponseEntity.badRequest().body("Параметры обязательны");
    }

    try {
        chatDataServise.deleteByUserLoginAndChatLogin(userLogin, chatLogin); // используем сервис
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        log.error("Ошибка при удалении: ", e);
        return ResponseEntity.internalServerError().body("Ошибка: " + e.getMessage());
    }
}
}