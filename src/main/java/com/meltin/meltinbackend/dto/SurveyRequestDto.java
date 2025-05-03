package com.meltin.meltinbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SurveyRequestDto {

    private Long userId;
    private String studentId;
    private String mbti;
    private String interest;
    private String communicationStyle;
    private String conflictResponse;
    private String preferredRole;
    private String preferredTeamMood;
    private String selfKeywords;
    private String matchingPreference;
}
