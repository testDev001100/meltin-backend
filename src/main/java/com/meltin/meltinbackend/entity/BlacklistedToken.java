package com.meltin.meltinbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiration;

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }
    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }


}
