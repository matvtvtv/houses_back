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
@Table(name = "users_houses")
public class UserModel {
    
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(unique = false, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String image;

    @Column(nullable = false)
    private String role;

    
}
