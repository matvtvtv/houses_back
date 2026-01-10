package com.houses_back.houses_back.service;

import org.springframework.stereotype.Service;

import com.houses_back.houses_back.model.UserModel;
import com.houses_back.houses_back.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel register(String login, String name, String password, String role) {
        if (userRepository.existsByLogin(login)) {
            throw new RuntimeException("Login already exists");
        }

        UserModel user = new UserModel();
        user.setLogin(login);
        user.setName(name);
        user.setPassword(password); // позже зашифруем
        user.setRole(role);
        user.setImage("");

        return userRepository.save(user);
    }

    public UserModel login(String login, String password) {
        return userRepository.findByLogin(login)
            .filter(user -> user.getPassword().equals(password))
            .orElseThrow(() -> new RuntimeException("Invalid login or password"));
    }



}