package com.houses_back.houses_back.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
import com.houses_back.houses_back.dto.TaskTemplateUpdateDTO;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskInstanceController {

    private final TaskService taskService;


     private final SimpMessagingTemplate messagingTemplate; // <-- добавлено

   @PutMapping("/instance/{instanceId}")
public ResponseEntity<TaskInstance> updateInstance(
        @PathVariable Long instanceId,
        @RequestBody TaskInstance update) {

    TaskInstance before = taskService.getInstance(instanceId);

    TaskInstance inst = taskService.updateInstance(instanceId, update);

    TaskInstanceDTO dto = taskService.toDto(inst);
    String chatLogin = inst.getTemplate().getChatLogin();

    messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);

    // ✅ ИСПРАВЛЕННАЯ ЛОГИКА
    if (!before.isConfirmedByParent()          // раньше НЕ было подтверждения
            && inst.isConfirmedByParent()      // теперь подтверждено
            && inst.isCompleted()) {            // и задача завершена
        try {
            taskService.awardCoinsAndLogStats(inst);

            // отправим обновлённые данные после начисления
            messagingTemplate.convertAndSend(
                    "/topic/tasks/" + chatLogin,
                    taskService.toDto(inst)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
    // Обновляем статус
    TaskInstance inst = taskService.patchInstanceStatus(instanceId, payload.isCompleted());

    // Если задача завершена - начисляем монеты и обновляем статистику
    if (payload.isCompleted()) {
        // Получаем полный объект с данными о пользователе
        TaskInstance fullInst = taskService.getInstance(instanceId);
        
     

        // Отправляем WS уведомление с полными данными
        String chatLogin = fullInst.getTemplate().getChatLogin();
        messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, taskService.toDto(fullInst));
    }

    return ResponseEntity.ok(inst);
}
// PATCH /api/tasks/instance/{id}/confirm
@PatchMapping("/{id}/confirm")
public ResponseEntity<TaskInstanceDTO> confirmByParent(@PathVariable Long id) {
    TaskInstance inst = taskService.confirmByParent(id);
    // Отправляем обновлённый DTO по WS (после начисления)
    TaskInstanceDTO dto = taskService.toDto(inst);
    String chatLogin = inst.getTemplate().getChatLogin();

    messagingTemplate.convertAndSend("/topic/tasks/" + chatLogin, dto);
    return ResponseEntity.ok(dto);
}


    // Вспомогательный класс payload
    public static class StatusPatch {
        private boolean completed;
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
   @PutMapping("/template/{templateId}")
public ResponseEntity<TaskTemplate> updateTemplate(@PathVariable Long templateId,
                                                   @RequestBody TaskTemplateUpdateDTO dto) {
    TaskTemplate updated = taskService.updateTemplate(templateId, dto);
    
    // Явное приведение к Object для разрешения неоднозначности
    messagingTemplate.convertAndSend("/topic/tasks/" + updated.getChatLogin(), 
        (Object) Map.of("type", "TEMPLATE_UPDATED", "templateId", templateId));
    
    return ResponseEntity.ok(updated);
}
}
