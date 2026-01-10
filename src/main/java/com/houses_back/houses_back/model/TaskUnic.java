package com.houses_back.houses_back.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TaskUnic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatLogin;

    private String userLogin;
    private String title;
    private String description;
    private int money;

}
