package com.meltin.meltinbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GptSurveyDto {
    private String name;
    private String mbti;
    private String interest;
    private String communicationStyle;
    private String conflictResponse;
    private String preferredRole;
    private String preferredTeamMood;
    private String selfKeyword;
    private String matchingPreference;
}
