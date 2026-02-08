package com.houses_back.houses_back.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_instance",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"template_id", "task_date"})})
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

    @Column(nullable = false)
    private boolean started;

    @Column(nullable = false)
    private boolean confirmedByParent;

    @Column(columnDefinition = "text")
    private String comment;

    @ElementCollection
    @CollectionTable(name = "task_instance_photos", joinColumns = @JoinColumn(name = "instance_id"))
    @Column(name = "photo_Base64", columnDefinition = "text")
    private List<String> photoBase64;

    private boolean completed;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}



