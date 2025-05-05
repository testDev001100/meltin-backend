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
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/matching/result")
    public ResponseEntity<?> getMatchingResult(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String role = jwtUtil.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        List<UserEntity> matchedUsers = userRepository.findAll()
                .stream()
                .filter(u -> u.getTeamNumber() != null)
                .collect(Collectors.toList());

        Map<Integer, List<Map<String, String>>> grouped = matchedUsers.stream()
                .collect(Collectors.groupingBy(
                        UserEntity::getTeamNumber,
                        Collectors.mapping(user -> Map.of(
                                "name", user.getName(),
                                "studentId", user.getStudentId()
                        ), Collectors.toList())
                ));

        List<Map<String, Object>> teamList = grouped.entrySet().stream()
                .map(entry -> Map.of(
                        "teamNumber", entry.getKey(),
                        "members", entry.getValue()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("teams", teamList));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String role = jwtUtil.getRole(token);

        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("권한 없음");
        }

        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> Map.of(
                        "name", user.getName(),
                        "studentId", user.getStudentId(),
                        "teamNumber", (Object) user.getTeamNumber()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("users", users));
    }
}

