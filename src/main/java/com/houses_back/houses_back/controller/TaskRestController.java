package com.houses_back.houses_back.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.model.Task;
import com.houses_back.houses_back.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/c")
public class TaskRestController {

    private final TaskRepository repository;

    @GetMapping("/{chatLogin}")
    public List<Task> getTasks(@PathVariable String chatLogin) {
        return repository.findByChatLoginOrderByCreatedAtAsc(chatLogin);
    }


    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Обновляем только поля, которые могут изменяться
        if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
        if (updatedTask.getDescription() != null) task.setDescription(updatedTask.getDescription());
        if (updatedTask.getMoney() > 0) task.setMoney(updatedTask.getMoney());
        if (updatedTask.getStartDate() != null) task.setStartDate(updatedTask.getStartDate());
        if (updatedTask.getDays() != null) task.setDays(updatedTask.getDays());
        if (updatedTask.getTargetLogin() != null) task.setTargetLogin(updatedTask.getTargetLogin());

        task.setRepeat(updatedTask.isRepeat());
        task.setCompleted(updatedTask.isCompleted());

        return repository.save(task);
    }

    // Частичное обновление флагов
    @PatchMapping("/{taskId}/status")
    public Task updateTaskStatus(@PathVariable Long taskId,
                                 @RequestBody Task statusUpdate) {
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setCompleted(statusUpdate.isCompleted());

        return repository.save(task);
    }
}
