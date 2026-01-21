package com.houses_back.houses_back.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "task_instance",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"template_id", "task_date"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private TaskTemplate template;

    @Column(name = "task_date", nullable = false)
    private LocalDate taskDate;

    private String userLogin;

    @Column(columnDefinition = "text")
    private String comment;

    @ElementCollection
    @CollectionTable(name = "task_instance_photos", joinColumns = @JoinColumn(name = "instance_id"))
    @Column(name = "photo_Base64", columnDefinition = "text")
    private List<String> photoBase64;

    private boolean completed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Гарантируем установку createdAt перед сохранением
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Можно добавить @PreUpdate для updatedAt
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


