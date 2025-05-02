package com.meltin.meltinbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {
    private String currentPassword;
    private String newPassword;
}
