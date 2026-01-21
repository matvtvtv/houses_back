package com.houses_back.houses_back.controller;

import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TaskService taskService;

    @PostMapping("/{chatLogin}")
    public ResponseEntity<?> createTemplate(@PathVariable String chatLogin, @RequestBody TaskTemplate template) {
        template.setChatLogin(chatLogin);
        TaskTemplate saved = taskService.createTemplate(template);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<?> updateTemplate(@PathVariable Long templateId, @RequestBody TaskTemplate updated) {
        TaskTemplate saved = taskService.updateTemplate(templateId, updated);
        return ResponseEntity.ok(saved);
    }
}
