package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.MyPageDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

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

        groupService.matchEligibleUsers();

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
                .map(user -> {
                    Map<String, Object> userMap = new java.util.HashMap<>();
                    userMap.put("name", user.getName());
                    userMap.put("studentId", user.getStudentId());
                    userMap.put("teamNumber", user.getTeamNumber());  // null 그대로 전달
                    return userMap;
                })
                .toList();

        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAdminInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        String role = jwtUtil.getRole(token);
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 없습니다.");
        }

        String username = jwtUtil.getUsername(token);
        UserEntity admin = userRepository.findByUsername(username);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없습니다.");
        }
        MyPageDTO dto = new MyPageDTO(
                admin.getUsername(),
                admin.getStudentId(),
                admin.getName(),
                admin.getRole()
        );

        return ResponseEntity.ok(dto);
    }
}

