package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.jwt.JWTUtil;
import com.meltin.meltinbackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyTeam(HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        UserEntity me = jwtUtil.getUserFromToken(token);

        if (me.getTeamNumber() == null) {
            return ResponseEntity.ok(Map.of(
                    "message", "아직 팀이 배정되지 않았습니다."
            ));
        }

        // 같은 팀의 팀원 전부 조회
        List<UserEntity> members = userRepository.findAllByTeamNumber(me.getTeamNumber());

        List<Map<String, String>> memberList = members.stream()
                .map(user -> Map.of(
                        "name", user.getName(),
                        "studentId", user.getStudentId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "teamNumber", me.getTeamNumber(),
                "members", memberList
        ));
    }
}
