package com.houses_back.houses_back.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class TaskTemplateUpdateDTO {
    private String title;
    private String description;
    private Integer money;
    private LocalDate startDate;
    private Boolean repeat;
    private List<String> repeatDays;
    private String targetLogin;
    private String startTime;
    private String endTime;
    private String partDay;
    private Integer importance;
}