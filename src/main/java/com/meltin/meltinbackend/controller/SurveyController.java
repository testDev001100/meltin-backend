package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.SurveyRequestDto;
import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.SurveyResponseRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import com.meltin.meltinbackend.service.SurveyService;
import com.meltin.meltinbackend.service.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<?> submitSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SurveyRequestDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(403).body("인증되지 않은 사용자입니다.");
        }

        return surveyService.submitSurvey(userDetails.getUsername(), dto);
    }
}
