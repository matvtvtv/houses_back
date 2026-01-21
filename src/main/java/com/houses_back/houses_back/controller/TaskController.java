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

    @MessageMapping("/tasks/c/{chatLogin}/create")
    public void createTask(@DestinationVariable String chatLogin, Task task) {
        task.setChatLogin(chatLogin);
        repository.save(task);

        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, task);
    }

    @MessageMapping("/tasks/c/{chatLogin}/update")
    public void updateTaskWs(@DestinationVariable String chatLogin, Task updatedTask) {
        Task task = repository.findById(updatedTask.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Обновляем поля
        if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
        if (updatedTask.getDescription() != null) task.setDescription(updatedTask.getDescription());
        if (updatedTask.getMoney() > 0) task.setMoney(updatedTask.getMoney());
        if (updatedTask.getStartDate() != null) task.setStartDate(updatedTask.getStartDate());
        if (updatedTask.getDays() != null) task.setDays(updatedTask.getDays());
        if (updatedTask.getTargetLogin() != null) task.setTargetLogin(updatedTask.getTargetLogin());

        task.setRepeat(updatedTask.isRepeat());
        task.setCompleted(updatedTask.isCompleted());

        repository.save(task);

        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, task);
    }
}
