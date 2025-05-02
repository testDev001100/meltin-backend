package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.SurveyRequestDto;
import com.meltin.meltinbackend.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<?> submitSurvey(@RequestHeader("Authorization") String token,
                                          @RequestBody SurveyRequestDto dto) {
        String pureToken = token.replace("Bearer ", "");
        return surveyService.submitSurvey(pureToken, dto);
    }
}
