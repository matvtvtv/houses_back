package com.houses_back.houses_back.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.houses_back.houses_back.dto.TaskInstanceDTO;
import com.houses_back.houses_back.dto.TaskTemplateUpdateDTO;
import com.houses_back.houses_back.model.ChatData;
import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;
import com.houses_back.houses_back.model.UserDailyStats;
import com.houses_back.houses_back.repository.ChatDataRepository;
import com.houses_back.houses_back.repository.TaskInstanceRepository;
import com.houses_back.houses_back.repository.TaskTemplateRepository;
import com.houses_back.houses_back.repository.UserDailyStatsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskTemplateRepository templateRepository;
    private final TaskInstanceRepository instanceRepository;
    private final UserDailyStatsRepository statsRepository;
    private final ChatDataRepository chatDataRepository;

    /**
     * Создать шаблон задачи.
     */
    @Transactional
    public TaskTemplate createTemplate(TaskTemplate template) {
        TaskTemplate saved = templateRepository.save(template);
        return saved;
    }

    /**
     * Обновить шаблон (принимает объект TaskTemplate с новыми полями)
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
        if (updated.getStartTime() != null) t.setStartTime(updated.getStartTime());
        if (updated.getEndTime() != null) t.setEndTime(updated.getEndTime());
        if (updated.getPartDay() != null) t.setPartDay(updated.getPartDay());
        if (updated.getImportance() > 0) t.setImportance(updated.getImportance());
        t.setRepeat(updated.isRepeat());
        if (updated.getTargetLogin() != null) t.setTargetLogin(updated.getTargetLogin());
        return templateRepository.save(t);
    }

    /**
     * Обновление шаблона на основе DTO (используется контроллером)
     */
    @Transactional
    public TaskTemplate updateTemplate(Long templateId, TaskTemplateUpdateDTO dto) {
        TaskTemplate t = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (dto.getTitle() != null) t.setTitle(dto.getTitle());
        if (dto.getDescription() != null) t.setDescription(dto.getDescription());
        if (dto.getMoney() != null) t.setMoney(dto.getMoney());
        if (dto.getStartDate() != null) t.setStartDate(dto.getStartDate());
        if (dto.getRepeat() != null) t.setRepeat(dto.getRepeat());
        if (dto.getRepeatDays() != null) t.setRepeatDays(dto.getRepeatDays());
        if (dto.getTargetLogin() != null) t.setTargetLogin(dto.getTargetLogin());
        if (dto.getStartTime() != null) t.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) t.setEndTime(dto.getEndTime());
        if (dto.getPartDay() != null) t.setPartDay(dto.getPartDay());
        if (dto.getImportance() != null) t.setImportance(dto.getImportance());

        return templateRepository.save(t);
    }

    /**
     * Получить/создать экземпляры задач для chatLogin в диапазоне дат.
     */
    @Transactional
    public List<TaskInstanceDTO> getOrCreateInstances(String chatLogin, LocalDate from, LocalDate to) {
        List<TaskTemplate> templates = templateRepository.findByChatLoginOrderByCreatedAtAsc(chatLogin);

        List<TaskInstanceDTO> result = new ArrayList<>();

        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            for (TaskTemplate tpl : templates) {
                if (matchesDate(tpl, cur)) {
                    Optional<TaskInstance> opt = instanceRepository.findByTemplateAndTaskDate(tpl, cur);
                    TaskInstance instance;
                    if (opt.isPresent()) {
                        instance = opt.get();
                    } else {
                        instance = TaskInstance.builder()
                                .template(tpl)
                                .taskDate(cur)
                                .started(false)
                                .confirmedByParent(false)
                                .completed(false)
                                .createdAt(LocalDateTime.now())
                                .build();
                        instance = instanceRepository.save(instance);
                    }
                    result.add(toDto(instance));
                }
            }
            cur = cur.plusDays(1);
        }

        return result.stream()
                .sorted((a, b) -> a.getTaskDate().compareTo(b.getTaskDate()))
                .collect(Collectors.toList());
    }

    private boolean matchesDate(TaskTemplate tpl, LocalDate date) {
        if (tpl.getStartDate() != null && date.isBefore(tpl.getStartDate())) {
            return false;
        }

        if (!tpl.isRepeat()) {
            if (tpl.getStartDate() == null) {
                return false;
            }
            return date.equals(tpl.getStartDate());
        }

        if (tpl.getRepeatDays() == null || tpl.getRepeatDays().isEmpty()) {
            if (tpl.getStartDate() == null) {
                return false;
            }
            return date.equals(tpl.getStartDate());
        } else {
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
        dto.setStarted(inst.isStarted());
        dto.setConfirmedByParent(inst.isConfirmedByParent());

        TaskTemplate tpl = inst.getTemplate();
        if (tpl != null) {
            dto.setTemplateId(tpl.getId());
            dto.setTitle(tpl.getTitle());
            dto.setDescription(tpl.getDescription());
            dto.setMoney(tpl.getMoney());
            dto.setChatLogin(tpl.getChatLogin());
            dto.setTargetLogin(tpl.getTargetLogin());
            dto.setRepeat(tpl.isRepeat());
            dto.setStartTime(tpl.getStartTime());
            dto.setEndTime(tpl.getEndTime());
            dto.setPartDay(tpl.getPartDay());
            dto.setImportance(tpl.getImportance());
            dto.setRepeatDays(tpl.getRepeatDays());
            dto.setTemplateUserLogin(tpl.getUserLogin());
        }

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
        inst.setStarted(update.isStarted());
        inst.setConfirmedByParent(update.isConfirmedByParent());
        inst.setUserLogin(update.getUserLogin());
        inst.setUpdatedAt(LocalDateTime.now());
        return instanceRepository.save(inst);
    }

    @Transactional
    public TaskInstance patchInstanceStatus(Long instanceId, boolean completed) {
        TaskInstance inst = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instance not found"));
        inst.setCompleted(completed);
        if (completed && !inst.isStarted()) {
            inst.setStarted(true);
        }
        inst.setUpdatedAt(LocalDateTime.now());
        return instanceRepository.save(inst);
    }

    @Transactional
    public TaskInstance confirmByParent(Long instanceId) {
        TaskInstance inst = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Instance not found"));

        if (!inst.isCompleted()) {
            throw new RuntimeException("Cannot confirm an uncompleted task");
        }

        if (!inst.isConfirmedByParent()) {
            inst.setConfirmedByParent(true);
            inst.setUpdatedAt(LocalDateTime.now());
            inst = instanceRepository.save(inst);
        }
        awardCoinsAndLogStats(inst);

        return inst;
    }

    public LocalDate findNearestDate(TaskTemplate template, LocalDate fromDate) {
        if (!template.isRepeat()) {
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }

        if (template.getRepeatDays() == null || template.getRepeatDays().isEmpty()) {
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }

        List<String> repeatDays = template.getRepeatDays().stream()
                .map(s -> s != null ? s.trim().toUpperCase() : "")
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (repeatDays.isEmpty()) {
            return template.getStartDate() != null ? template.getStartDate() : LocalDate.now();
        }

        LocalDate current = fromDate;
        if (template.getStartDate() != null && current.isBefore(template.getStartDate())) {
            current = template.getStartDate();
        }

        for (int i = 0; i < 7; i++) {
            String currentDay = current.getDayOfWeek().toString();
            if (repeatDays.contains(currentDay)) {
                return current;
            }
            current = current.plusDays(1);
        }

        return fromDate;
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
                .started(false)
                .confirmedByParent(false)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();
        return instanceRepository.save(instance);
    }

    @Transactional
    public void awardCoinsAndLogStats(TaskInstance instance) {
        if (instance.getUserLogin() == null || instance.getUserLogin().isEmpty()) {
            return;
        }

        String userLogin = instance.getUserLogin();
        String chatLogin = instance.getTemplate().getChatLogin();
        int money = instance.getTemplate().getMoney();

        // 1. Начислить монеты в ChatData
        ChatData chatData = chatDataRepository.findByChatLoginAndUserLogin(chatLogin, userLogin)
                .orElseThrow(() -> new RuntimeException("ChatData not found for user: " + userLogin));
        chatData.setMoney(chatData.getMoney() + money);
        chatDataRepository.save(chatData);

        // 2. Обновить дневную статистику
        LocalDate today = LocalDate.now();
        UserDailyStats stats = statsRepository
                .findByUserLoginAndChatLoginAndDate(userLogin, chatLogin, today)
                .orElseGet(() -> UserDailyStats.builder()
                        .userLogin(userLogin)
                        .chatLogin(chatLogin)
                        .date(today)
                        .completedTasksCount(0)
                        .earnedMoney(0)
                        .build());

        stats.setCompletedTasksCount(stats.getCompletedTasksCount() + 1);
        stats.setEarnedMoney(stats.getEarnedMoney() + money);

        statsRepository.save(stats);

        System.out.println("Stats updated for " + userLogin + ": tasks=" +
                stats.getCompletedTasksCount() + ", money=" + stats.getEarnedMoney());
    }

    // Вспомогательный метод получения экземпляра
    public TaskInstance getInstance(Long instanceId) {
        return instanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("TaskInstance not found: " + instanceId));
    }
    @Transactional
public void deleteInstance(Long instanceId) {
    if (!instanceRepository.existsById(instanceId)) {
        throw new RuntimeException("Instance not found: " + instanceId);
    }
    instanceRepository.deleteById(instanceId);
}

@Transactional
public void deleteTemplate(Long templateId) {
    TaskTemplate tpl = templateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));

    // удалить все экземпляры, связанные с шаблоном
    List<TaskInstance> instances = instanceRepository.findByTemplate(tpl);
    if (instances != null && !instances.isEmpty()) {
        instanceRepository.deleteAll(instances);
    }

    templateRepository.deleteById(templateId);
}

    
}
