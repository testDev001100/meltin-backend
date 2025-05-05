package com.meltin.meltinbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveyResponseDto {
    private String name;
    private String studentId;
    private String mbti;
    private String communicationStyle;
    private String conflictResponse;
    private String preferredRole;
    private String preferredTeamMood;
    private String interests;
    private String selfKeywords;
    private String matchingPreference;
}