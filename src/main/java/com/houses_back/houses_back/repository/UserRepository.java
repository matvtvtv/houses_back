package com.houses_back.houses_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.houses_back.houses_back.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByLogin(String login);

    boolean existsByLogin(String login);
}