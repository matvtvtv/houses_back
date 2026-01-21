package com.houses_back.houses_back.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.houses_back.houses_back.dto.TaskInstanceDTO;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.repository.TaskInstanceRepository;
import com.houses_back.houses_back.service.TaskService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class TaskWsController {
    private final TaskService taskService;
    private final TaskInstanceRepository instanceRepository;
    private final SimpMessagingTemplate messagingTemplate; // <-- ДОБАВЬТЕ ЭТУ СТРОКУ

    @MessageMapping("/ws/tasks/{chatLogin}/create")
    public void createTaskWs(@DestinationVariable String chatLogin, TaskTemplate template) {
        template.setChatLogin(chatLogin);
        TaskTemplate saved = taskService.createTemplate(template);
        
        LocalDate nearestDate = taskService.findNearestDate(saved, LocalDate.now());
        TaskInstance instance = taskService.findOrCreateInstanceForTemplate(saved, nearestDate);
        
        TaskInstanceDTO dto = taskService.toDto(instance);
        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);
    }

    @MessageMapping("/ws/tasks/{chatLogin}/update")
    public void updateTaskWs(@DestinationVariable String chatLogin, TaskTemplate template) {
        TaskTemplate saved = taskService.updateTemplate(template.getId(), template);
        
        List<TaskInstance> instances = instanceRepository.findByTemplate(saved);
        for (TaskInstance inst : instances) {
            TaskInstanceDTO dto = taskService.toDto(inst);
            messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);
        }
    }
}