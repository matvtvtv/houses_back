package com.houses_back.houses_back.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.TaskInstanceDTO;
import com.houses_back.houses_back.dto.TaskTemplateUpdateDTO;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.service.TaskService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskInstanceController {

    private final TaskService taskService;
    private final SimpMessagingTemplate messagingTemplate;

    @PutMapping("/instance/{instanceId}")
    public ResponseEntity<TaskInstance> updateInstance(
            @PathVariable Long instanceId,
            @RequestBody TaskInstance update) {

        TaskInstance before = taskService.getInstance(instanceId);
        TaskInstance inst = taskService.updateInstance(instanceId, update);

        TaskInstanceDTO dto = taskService.toDto(inst);
        String chatLogin = inst.getTemplate().getChatLogin();
        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);

        if (!before.isConfirmedByParent() && inst.isConfirmedByParent() && inst.isCompleted()) {
            try {
                taskService.awardCoinsAndLogStats(inst);
                messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, taskService.toDto(inst));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return ResponseEntity.ok(inst);
    }

    @GetMapping("/{chatLogin}")
    public ResponseEntity<List<TaskInstanceDTO>> getTasks(
            @PathVariable String chatLogin,
            @RequestParam("from") String fromStr,
            @RequestParam("to") String toStr
    ) {
        LocalDate from = LocalDate.parse(fromStr);
        LocalDate to = LocalDate.parse(toStr);
        List<TaskInstanceDTO> list = taskService.getOrCreateInstances(chatLogin, from, to);

        System.out.println("Request chatLogin=" + chatLogin + " from=" + from + " to=" + to);
        System.out.println("Found tasks=" + list.size());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/instance/{instanceId}/status")
    public ResponseEntity<TaskInstance> patchStatus(@PathVariable Long instanceId,
                                                    @RequestBody StatusPatch payload) {

        TaskInstance inst = taskService.patchInstanceStatus(instanceId, payload.isCompleted());
        if (payload.isCompleted()) {
            TaskInstance fullInst = taskService.getInstance(instanceId);
            String chatLogin = fullInst.getTemplate().getChatLogin();
            messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, taskService.toDto(fullInst));
        }

        return ResponseEntity.ok(inst);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<TaskInstanceDTO> confirmByParent(@PathVariable Long id) {
        TaskInstance inst = taskService.confirmByParent(id);
        TaskInstanceDTO dto = taskService.toDto(inst);
        String chatLogin = inst.getTemplate().getChatLogin();
        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/template/{templateId}")
    public ResponseEntity<TaskTemplate> updateTemplate(@PathVariable Long templateId,
                                                       @RequestBody TaskTemplateUpdateDTO dto) {
        TaskTemplate updated = taskService.updateTemplate(templateId, dto);
        messagingTemplate.convertAndSend("/topic/tasks/" + updated.getChatLogin(),
                (Object) Map.of("type", "TEMPLATE_UPDATED", "templateId", templateId));
        return ResponseEntity.ok(updated);
    }

    // 🔥 Удаление instance с уведомлением WS
    @DeleteMapping("/instance/{instanceId}")
    public ResponseEntity<?> deleteInstance(@PathVariable Long instanceId) {
        TaskInstance inst = taskService.getInstance(instanceId);
        taskService.deleteInstance(instanceId);

        DeleteEvent event = new DeleteEvent();
        event.setType("TASK_DELETED");
        event.setInstanceId(instanceId);
        event.setTemplate(false);
        event.setTemplateId(inst.getTemplate().getId());

        messagingTemplate.convertAndSend("/topic/tasks/" + inst.getTemplate().getChatLogin(), event);

        return ResponseEntity.noContent().build();
    }

    public static class StatusPatch {
        private boolean completed;
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    @Data
    public static class DeleteEvent {
        private String type;
        private Long instanceId;
        private Long templateId;
        private boolean isTemplate;
    }
}
