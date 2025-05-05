package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.meltin.meltinbackend.dto.GptSurveyDto;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api.key}")
    private String apikey;

    private final RestTemplate restTemplate;

    public String createPrompt(List<SurveyResponseEntity> surveys) {
        StringBuilder prompt = new StringBuilder("다음 사용자들을 성향이 비슷한 팀으로 4명씩 나눠주세요.\n\n");

        for (SurveyResponseEntity survey : surveys) {
            GptSurveyDto dto = toGptDto(survey);
            prompt.append(String.format(
                    "이름: %s, MBTI: %s, 소통: %s, 갈등: %s, 역할: %s, 분위기: %s, 관심사: %s, 키워드: %s, 성향: %s\n",
                    dto.getName(), dto.getMbti(), dto.getCommunicationStyle(), dto.getConflictResponse(),
                    dto.getPreferredRole(), dto.getPreferredTeamMood(), dto.getInterest(),
                    dto.getSelfKeyword(), dto.getMatchingPreference()
                    ));
        }
        prompt.append("\n출력 형식:\nTeam 1: 이름1, 이름2, ...\nTeam 2: ...");
        return prompt.toString();
    }

    public String callGpt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apikey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4");
        body.put("messages", List.of(
                Map.of("role", "user", "content", prompt)));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                entity,
                Map.class
        );
        Map<String, Object> choice = (Map<String, Object>) ((List<Object>) response.getBody().get("choices")).get(0);
        Map<String, Object> message = (Map<String, Object>) choice.get("message");

        return message.get("content").toString();
    }

    private GptSurveyDto toGptDto(SurveyResponseEntity entity) {
        return new GptSurveyDto(
                entity.getUser().getName(),
                entity.getMbti(),
                entity.getInterests(),
                entity.getCommunicationStyle(),
                entity.getConflictResponse(),
                entity.getPreferredRole(),
                entity.getPreferredTeamMood(),
                entity.getSelfKeywords(),
                entity.getMatchingPreference()
        );
    }
}
