package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.SurveyResponseDto;
import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.jwt.JWTUtil;
import com.meltin.meltinbackend.repository.SurveyResponseRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import com.meltin.meltinbackend.service.GptService;
import com.meltin.meltinbackend.service.GroupService;
import com.meltin.meltinbackend.service.SurveyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final SurveyResponseRepository surveyResponseRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final GptService gptService;
    private final JWTUtil jwtUtil;
    private final SurveyService surveyService;

    @PostMapping("/match")
    public ResponseEntity<String> match(@RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "").trim();

        UserEntity admin = jwtUtil.getUserFromToken(token);
        if (!admin.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("접근 불가: 관리자 권한 필요");
        }

        List<SurveyResponseEntity> surveys = surveyResponseRepository.findAll();
        String prompt = gptService.createPrompt(surveys);
        String gptResponse = gptService.callGpt(prompt);
        groupService.applyGroupingResult(gptResponse);

        return ResponseEntity.ok("그룹핑 완료");
    }

    @GetMapping("/surveys")
    public ResponseEntity<?> getAllSurveys(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String role = jwtUtil.getRole(token);

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        List<SurveyResponseDto> all = surveyService.getAllSurveyResponses();
        return ResponseEntity.ok(all);
    }
}

