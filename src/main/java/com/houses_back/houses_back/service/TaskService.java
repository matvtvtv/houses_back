package com.houses_back.houses_back.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.houses_back.houses_back.dto.TaskInstanceDTO;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.repository.TaskInstanceRepository;
import com.houses_back.houses_back.repository.TaskTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskTemplateRepository templateRepository;
    private final TaskInstanceRepository instanceRepository;

    /**
     * Создать шаблон задачи.
     */
    @Transactional
    public TaskTemplate createTemplate(TaskTemplate template) {
        TaskTemplate saved = templateRepository.save(template);
        return saved;
    }

    /**
     * Обновить шаблон.
     */
    @Transactional
    public TaskTemplate updateTemplate(Long templateId, TaskTemplate updated) {
        TaskTemplate t = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        if (updated.getTitle() != null) t.setTitle(updated.getTitle());
        if (updated.getDescription() != null) t.setDescription(updated.getDescription());
        if (updated.getMoney() > 0) t.setMoney(updated.getMoney());
        if (updated.getStartDate() != null) t.setStartDate(updated.getStartDate());
        if (updated.getRepeatDays() != null) t.setRepeatDays(updated.getRepeatDays());
        t.setRepeat(updated.isRepeat());
        if (updated.getTargetLogin() != null) t.setTargetLogin(updated.getTargetLogin());
        return templateRepository.save(t);
    }

   
    @Transactional
    public List<TaskInstanceDTO> getOrCreateInstances(String chatLogin, LocalDate from, LocalDate to) {
        // 1. получить все шаблоны для чата
        List<TaskTemplate> templates = templateRepository.findByChatLoginOrderByCreatedAtAsc(chatLogin);

        List<TaskInstanceDTO> result = new ArrayList<>();

        // 2. для каждой даты в диапазоне формируем экземпляры
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            for (TaskTemplate tpl : templates) {
                // проверка, должна ли задача появиться на эту дату
                if (matchesDate(tpl, cur)) {
                    // проверить, есть ли уже экземпляр
                    Optional<TaskInstance> opt = instanceRepository.findByTemplateAndTaskDate(tpl, cur);
                    TaskInstance instance;
                    if (opt.isPresent()) {
                        instance = opt.get();
                    } else {
                        // создаём новый экземпляр на эту дату
                        instance = TaskInstance.builder()
                                .template(tpl)
                                .taskDate(cur)
                                .completed(false)
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                        instance = instanceRepository.save(instance);
                    }
                    result.add(toDto(instance));
                }
            }
            cur = cur.plusDays(1);
        }

        // сортировать по дате
        return result.stream()
                .sorted((a, b) -> a.getTaskDate().compareTo(b.getTaskDate()))
                .collect(Collectors.toList());
    }

   private boolean matchesDate(TaskTemplate tpl, LocalDate date) {
    // Если дата начала выбрана и текущая дата раньше - не показывать
    if (tpl.getStartDate() != null && date.isBefore(tpl.getStartDate())) {
        return false;
    }

    // Если задача не повторяется
    if (!tpl.isRepeat()) {
        LocalDate targetDate = (tpl.getStartDate() != null) ? tpl.getStartDate() : LocalDate.now();
        return date.equals(targetDate);
    }

    // Задача повторяется
    if (tpl.getRepeatDays() == null || tpl.getRepeatDays().isEmpty()) {
        // Дни не выбраны - считаем что не повторяется
        LocalDate targetDate = (tpl.getStartDate() != null) ? tpl.getStartDate() : LocalDate.now();
        return date.equals(targetDate);
    } else {
        // Проверяем день недели
        String dowName = date.getDayOfWeek().toString();
        return tpl.getRepeatDays().stream()
                .anyMatch(s -> s != null && s.trim().equalsIgnoreCase(dowName));
    }

}

    public TaskInstanceDTO toDto(TaskInstance inst) {
        TaskInstanceDTO dto = new TaskInstanceDTO();
        dto.setInstanceId(inst.getId());
        dto.setTaskDate(inst.getTaskDate());
        dto.setCompleted(inst.isCompleted());
        dto.setComment(inst.getComment());
        dto.setPhotoBase64(inst.getPhotoBase64());
        dto.setUserLogin(inst.getUserLogin());

        TaskTemplate tpl = inst.getTemplate();
        dto.setTemplateId(tpl.getId());
        dto.setTitle(tpl.getTitle());
        dto.setDescription(tpl.getDescription());
        dto.setMoney(tpl.getMoney());
        dto.setChatLogin(tpl.getChatLogin());
        dto.setTargetLogin(tpl.getTargetLogin());
        dto.setRepeat(tpl.isRepeat());
        dto.setRepeatDays(tpl.getRepeatDays());
        dto.setTemplateUserLogin(tpl.getUserLogin());
        return dto;
    }

    /**
     * Обновление экземпляра: коммент, фото, completed
     */
    @Transactional
    public TaskInstance updateInstance(Long instanceId, TaskInstance update) {
        TaskInstance inst = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instance not found"));
        if (update.getComment() != null) inst.setComment(update.getComment());
        if (update.getPhotoBase64() != null) inst.setPhotoBase64(update.getPhotoBase64());
        inst.setCompleted(update.isCompleted());
        inst.setUserLogin(update.getUserLogin());
        inst.setUpdatedAt(java.time.LocalDateTime.now());
        return instanceRepository.save(inst);
    }

    @Transactional
    public TaskInstance patchInstanceStatus(Long instanceId, boolean completed) {
        TaskInstance inst = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instance not found"));
        inst.setCompleted(completed);
        inst.setUpdatedAt(java.time.LocalDateTime.now());
        return instanceRepository.save(inst);
    }
    
    public LocalDate findNearestDate(TaskTemplate template, LocalDate fromDate) {
        if (!template.isRepeat()) {
            // Не повторяется - возвращаем startDate или сегодня
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }
        
        if (template.getRepeatDays() == null || template.getRepeatDays().isEmpty()) {
            // Повторяется, но дни не указаны - считаем что не повторяется
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }
        
        // Нормализуем дни недели
        List<String> repeatDays = template.getRepeatDays().stream()
                .map(s -> s != null ? s.trim().toUpperCase() : "")
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        if (repeatDays.isEmpty()) {
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }
        
        LocalDate current = fromDate;
        // Если есть startDate, начинаем с неё
        if (template.getStartDate() != null && current.isBefore(template.getStartDate())) {
            current = template.getStartDate();
        }
        
        // Ищем ближайший подходящий день (максимум 7 итераций)
        for (int i = 0; i < 7; i++) {
            String currentDay = current.getDayOfWeek().toString();
            if (repeatDays.contains(currentDay)) {
                return current;
            }
            current = current.plusDays(1);
        }
        
        return fromDate; // fallback
    }

    /**
     * Создать или получить существующий экземпляр задачи
     */
    @Transactional
    public TaskInstance findOrCreateInstanceForTemplate(TaskTemplate template, LocalDate date) {
        Optional<TaskInstance> existing = instanceRepository.findByTemplateAndTaskDate(template, date);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        TaskInstance instance = TaskInstance.builder()
                .template(template)
                .taskDate(date)
                .completed(false)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        return instanceRepository.save(instance);
    }
}
