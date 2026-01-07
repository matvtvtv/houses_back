package com.houses_back.houses_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name="chats_data")
public class ChatData{

    @Id   
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String chatLogin;
    private String userLogin;
    private String userRole;
    
}