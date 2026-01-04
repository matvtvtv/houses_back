package com.houses_back.houses_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.houses_back.houses_back.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByChatIdOrderByCreatedAtAsc(String chatId);
}