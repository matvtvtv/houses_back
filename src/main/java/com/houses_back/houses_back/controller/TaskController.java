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

    @MessageMapping("/tasks/{chatId}/create")
    public void createTask(
            @DestinationVariable String chatId,
            Task task
    ) {
        task.setChatId(chatId);
        repository.save(task);

        messagingTemplate.convertAndSend(
                "/topic/tasks/" + chatId,
                task
        );
    }
}