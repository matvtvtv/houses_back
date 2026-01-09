package com.houses_back.houses_back.model;

import jakarta.persistence.Column;
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

    @Column(name = "chat_login")
    private String chatLogin;

    @Column(name = "chat_name")
    private String chatName;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "money")
    private int money;
    
}