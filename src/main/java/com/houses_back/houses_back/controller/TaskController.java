package com.houses_back.houses_back.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.houses_back.houses_back.model.Task;
import com.houses_back.houses_back.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/tasks/{chatLogin}/create")
    public void createTask(
            @DestinationVariable String chatLogin,
            Task task
    ) {
        task.setChatLogin(chatLogin);
        repository.save(task);

        messagingTemplate.convertAndSend(
                "/topic/tasks/" + chatLogin,
                task
        );
    }
}