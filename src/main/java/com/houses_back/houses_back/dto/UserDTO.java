package com.houses_back.houses_back.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {
    private String login;
    private String name;
    private String password;
    private String role;
}
