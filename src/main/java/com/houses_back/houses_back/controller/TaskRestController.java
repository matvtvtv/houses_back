package com.houses_back.houses_back.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.model.Task;
import com.houses_back.houses_back.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskRepository repository;

    @GetMapping("/{chatLogin}")
    public List<Task> getTasks(@PathVariable String chatLogin) {
        return repository.findByChatLoginOrderByCreatedAtAsc(chatLogin);
    }
}
