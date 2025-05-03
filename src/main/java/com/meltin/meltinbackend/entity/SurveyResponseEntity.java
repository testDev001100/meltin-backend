package com.meltin.meltinbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "survey_response")
@NoArgsConstructor
public class SurveyResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String studentId;
    private String mbti;

    @Column(length = 500)
    private String interests;

    private String communicationStyle;
    private String conflictResponse;
    private String preferredRole;
    private String preferredTeamMood;
    @Column(length = 255)
    private String selfKeywords;

    private String matchingPreference;
}
