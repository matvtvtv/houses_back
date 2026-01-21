package com.houses_back.houses_back.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TaskInstanceDTO {
    // instance fields
    private Long instanceId;
    private LocalDate taskDate;
    private boolean completed;
    private String comment;
    private List<String> photoBase64;
    private String userLogin; // кто выполнил

    // template fields (копируем для удобства клиента)
    private Long templateId;
    private String title;
    private String description;
    private int money;
    private String chatLogin;
    private String targetLogin;
    private boolean repeat;
    private List<String> repeatDays;
    private String templateUserLogin;
}
