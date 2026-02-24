package com.houses_back.houses_back.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.service.TaskService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TaskService taskService;
    private final SimpMessagingTemplate messagingTemplate;

    // ===================== CREATE =====================
    @PostMapping("/{chatLogin}")
    public ResponseEntity<?> createTemplate(
            @PathVariable String chatLogin,
            @RequestBody TaskTemplate template) {

        template.setChatLogin(chatLogin);
        TaskTemplate saved = taskService.createTemplate(template);

        // 🔥 WS ПИНОК СОЗДАНИЯ
        UpdateEvent event = UpdateEvent.templateCreated(saved.getId());

        messagingTemplate.convertAndSend(
                "/topic/tasks/" + chatLogin,
                event
        );

        return ResponseEntity.ok(saved);
    }

    // ===================== UPDATE =====================
    @PutMapping("/{templateId}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody TaskTemplate updated) {

        TaskTemplate saved = taskService.updateTemplate(templateId, updated);

        // 🔥 WS ПИНОК ОБНОВЛЕНИЯ
        UpdateEvent event = UpdateEvent.templateUpdated(templateId);

        messagingTemplate.convertAndSend(
                "/topic/tasks/" + saved.getChatLogin(),
                event
        );

        return ResponseEntity.ok(saved);
    }

    // ===================== DELETE =====================
    @DeleteMapping("/{templateId}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId) {

        TaskTemplate template = taskService.getTemplate(templateId);
        taskService.deleteTemplate(templateId);

        UpdateEvent event = UpdateEvent.templateDeleted(templateId);

        messagingTemplate.convertAndSend(
                "/topic/tasks/" + template.getChatLogin(),
                event
        );

        return ResponseEntity.noContent().build();
    }

    // ==================================================
    // 🔥 УНИВЕРСАЛЬНОЕ СОБЫТИЕ
    // ==================================================
    @Data
    public static class UpdateEvent {

        private String type;        // TEMPLATE_CREATED / UPDATED / DELETED
        private Long templateId;
        private Long instanceId;
        private boolean template;

        public static UpdateEvent templateCreated(Long id) {
            UpdateEvent e = new UpdateEvent();
            e.type = "TEMPLATE_CREATED";
            e.templateId = id;
            e.template = true;
            return e;
        }

        public static UpdateEvent templateUpdated(Long id) {
            UpdateEvent e = new UpdateEvent();
            e.type = "TEMPLATE_UPDATED";
            e.templateId = id;
            e.template = true;
            return e;
        }

        public static UpdateEvent templateDeleted(Long id) {
            UpdateEvent e = new UpdateEvent();
            e.type = "TEMPLATE_DELETED";
            e.templateId = id;
            e.template = true;
            return e;
        }
    }
}