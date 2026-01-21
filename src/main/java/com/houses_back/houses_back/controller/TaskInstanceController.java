package com.houses_back.houses_back.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.houses_back.houses_back.dto.TaskInstanceDTO;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskInstanceController {

    private final TaskService taskService;


     private final SimpMessagingTemplate messagingTemplate; // <-- добавлено

    @PutMapping("/instance/{instanceId}")
    public ResponseEntity<TaskInstance> updateInstance(@PathVariable Long instanceId,
                                                       @RequestBody TaskInstance update) {
        TaskInstance inst = taskService.updateInstance(instanceId, update);

        // Отправляем по WS обновлённый DTO
        TaskInstanceDTO dto = taskService.toDto(inst);
        String chatLogin = inst.getTemplate().getChatLogin();
        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);

        return ResponseEntity.ok(inst);
    }
    /**
     * Получить/создать экземпляры задач для chatLogin в диапазоне дат.
     * Пример: GET /api/tasks/{chatLogin}?from=2026-01-01&to=2026-01-14
     */
    @GetMapping("/{chatLogin}")
    public ResponseEntity<List<TaskInstanceDTO>> getTasks(
            @PathVariable String chatLogin,
            @RequestParam("from") String fromStr,
            @RequestParam("to") String toStr
    ) {
        LocalDate from = LocalDate.parse(fromStr);
        LocalDate to = LocalDate.parse(toStr);
        List<TaskInstanceDTO> list = taskService.getOrCreateInstances(chatLogin, from, to);
        return ResponseEntity.ok(list);
    }

  
    @PatchMapping("/instance/{instanceId}/status")
    public ResponseEntity<TaskInstance> patchStatus(@PathVariable Long instanceId,
                                                    @RequestBody StatusPatch payload) {
        TaskInstance inst = taskService.patchInstanceStatus(instanceId, payload.isCompleted());
        return ResponseEntity.ok(inst);
    }

    // Вспомогательный класс payload
    public static class StatusPatch {
        private boolean completed;
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
}
