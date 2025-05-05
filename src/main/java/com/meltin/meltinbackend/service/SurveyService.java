package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.dto.SurveyRequestDto;
import com.meltin.meltinbackend.dto.SurveyResponseDto;
import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.jwt.JWTUtil;
import com.meltin.meltinbackend.repository.SurveyResponseRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final UserRepository userRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final JWTUtil jwtUtil;

    public ResponseEntity<String> submitSurvey(String token, SurveyRequestDto dto) {
        String username = jwtUtil.extractUsername(token);
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유저를 찾을 수 없습니다.");
        }

        if (surveyResponseRepository.findByUser(user).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 설문을 제출했습니다.");
        }

        SurveyResponseEntity response = new SurveyResponseEntity();
        response.setUser(user);
        response.setStudentId(dto.getStudentId());
        response.setMbti(dto.getMbti());
        response.setCommunicationStyle(dto.getCommunicationStyle());
        response.setInterests(dto.getInterest());
        response.setConflictResponse(dto.getConflictResponse());
        response.setPreferredRole(dto.getPreferredRole());
        response.setPreferredTeamMood(dto.getPreferredTeamMood());
        response.setSelfKeywords(String.join(",", dto.getSelfKeywords())); // ✅ CSV 저장
        response.setMatchingPreference(dto.getMatchingPreference());

        surveyResponseRepository.save(response);
        return ResponseEntity.ok("설문 제출 성공");
    }

    public List<SurveyResponseDto> getAllSurveyResponses() {
        List<SurveyResponseEntity> responses = surveyResponseRepository.findAll();

        return responses.stream()
                .map(r -> new SurveyResponseDto(
                        r.getUser().getName(),
                        r.getUser().getStudentId(),
                        r.getMbti(),
                        r.getCommunicationStyle(),
                        r.getConflictResponse(),
                        r.getPreferredRole(),
                        r.getPreferredTeamMood(),
                        r.getInterests(),
                        r.getSelfKeywords(),
                        r.getMatchingPreference()
                ))
                .collect(Collectors.toList());
    }
}
