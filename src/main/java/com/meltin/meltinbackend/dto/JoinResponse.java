package com.meltin.meltinbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinResponse {
    private String message;

    public JoinResponse(String message) {
        this.message = message;
    }
}

