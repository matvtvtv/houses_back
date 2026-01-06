package com.houses_back.houses_back.service;

import org.springframework.stereotype.Service;

import com.houses_back.houses_back.model.UserModel;
import com.houses_back.houses_back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel register(String login, String password, String role) {
        if (userRepository.existsByLogin(login)) {
            throw new RuntimeException("Login already exists");
        }

        UserModel user = new UserModel();
        user.setLogin(login);
        user.setPassword(password); 
        user.setRole(role);
        user.setMoney(0);
        user.setImage("");
        return userRepository.save(user);
    }
}