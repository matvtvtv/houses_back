package com.houses_back.houses_back.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.EnteranceDTO;
import com.houses_back.houses_back.dto.UserDTO;
import com.houses_back.houses_back.model.UserModel;
import com.houses_back.houses_back.repository.UserRepository;
import com.houses_back.houses_back.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;


    @PostMapping("/register")
    public UserModel register(@RequestBody UserDTO request) {
        return userService.register(
            request.getLogin(),
            request.getName(),
            request.getPassword(),
            request.getRole()
        );
    }
    @PostMapping("/login")
    public UserModel login(@RequestBody EnteranceDTO request) {
        return userService.login(
            request.getLogin(),
            request.getPassword()
        );
    }

    @GetMapping("/enter/{login}")
    public Optional<UserModel> getMethodName(@PathVariable String login) {
        return userRepository.findByLogin(login);
    }
    

}
