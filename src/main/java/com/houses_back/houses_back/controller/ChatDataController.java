package com.houses_back.houses_back.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.ChatDataDTO;
import com.houses_back.houses_back.dto.JoinChatDataDTO;
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
        public List <ChatData> findByUserLogin(@PathVariable String userLogin){
        return chatDataServise.findByUserLogin(userLogin);
    }
     @GetMapping("/get_chats_users/{chatLogin}")
        public List <ChatData> findByChatLogin(@PathVariable String chatLogin){
        return chatDataServise.findByChatLogin(chatLogin);
    }


}
