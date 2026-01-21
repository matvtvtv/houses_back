package com.houses_back.houses_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.houses_back.houses_back.model.TaskTemplate;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {
    List<TaskTemplate> findByChatLoginOrderByCreatedAtAsc(String chatLogin);
}
