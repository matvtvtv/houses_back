package com.houses_back.houses_back.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.houses_back.houses_back.model.TaskInstance;
import com.houses_back.houses_back.model.TaskTemplate;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {

    List<TaskInstance> findByTemplate_ChatLoginAndTaskDateBetweenOrderByTaskDateAsc(String chatLogin, LocalDate from, LocalDate to);

    List<TaskInstance> findByTemplate_ChatLoginAndTaskDateOrderByTaskDateAsc(String chatLogin, LocalDate date);

    Optional<TaskInstance> findByTemplateAndTaskDate(TaskTemplate template, LocalDate date);

    List<TaskInstance> findByTemplate(TaskTemplate template);
    

}
