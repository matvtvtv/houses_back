package com.houses_back.houses_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.houses_back.houses_back.model.ChatData;


public interface ChatDataRepository extends JpaRepository<ChatData, Long> {

    Optional<ChatData> findByChatLoginAndUserLogin(String chatLogin, String userLogin);

    boolean existsByChatLoginAndUserLogin(String chatLogin, String userLogin);
}
