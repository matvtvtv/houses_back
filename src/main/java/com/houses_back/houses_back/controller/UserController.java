package com.houses_back.houses_back.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.RegisterRequest;
import com.houses_back.houses_back.model.UserModel;
import com.houses_back.houses_back.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserModel register(@RequestBody RegisterRequest request) {
    return userService.register(
            request.getLogin(),
            request.getPassword(),
            request.getRole()
    );

    
}
}
