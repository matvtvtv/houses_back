package com.houses_back.houses_back.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatLogin;

    private String userLogin;//кто задал
    private String targetLogin;//кому, если пустое, то всем 



    private String title;
    private String description;
    private int money;
    

    private boolean execution = false;

    private boolean completed = false;


    private LocalDateTime createdAt = LocalDateTime.now();
}
