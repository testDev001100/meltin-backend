package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.jwt.JWTUtil;
import com.meltin.meltinbackend.repository.SurveyResponseRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import com.meltin.meltinbackend.service.GptService;
import com.meltin.meltinbackend.service.GroupService;
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

    @PostMapping("/match")
    public ResponseEntity<String> match(@RequestHeader("Authorization") String token) {
        UserEntity admin = jwtUtil.getUserFromToken(token);
        if (!admin.getRole().equals("ADMIN")) {
            return ResponseEntity.status(403).body("접근 불가: 관리자 권한 필요");
        }

        List<SurveyResponseEntity> surveys = surveyResponseRepository.findAll();
        String prompt = gptService.createPrompt(surveys);
        String gptResponse = gptService.callGpt(prompt);
        groupService.applyGroupingResult(gptResponse);

        return ResponseEntity.ok("그룹핑 완료");
    }

}
