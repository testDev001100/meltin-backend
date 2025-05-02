package com.meltin.meltinbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SurveyRequestDto {

    private String studentId;
    private String mbti;
    private String personality;
    private String communicationStyle;
    private String interests;
}
