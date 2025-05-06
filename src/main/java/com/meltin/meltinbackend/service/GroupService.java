package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.SurveyResponseRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserRepository userRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final GptService gptService;

    public void applyGroupingResult(String gptResponse) {
        String[] lines = gptResponse.split("\\n");
        for (String line : lines) {
            if (line.startsWith("Team")) {
                int teamNumber = Integer.parseInt(line.split(" ")[1].replace(":", ""));
                String[] names = line.substring(line.indexOf(":") + 1).split(",");
                for (String rawName : names) {
                    String name = rawName.trim();
                    System.out.println("GPT 이름 추출: '" + name + "'");
                    Optional<UserEntity> userOpt = userRepository.findByName(name);
                    userOpt.ifPresent(user -> {
                        user.setTeamNumber(teamNumber);
                        userRepository.save(user);
                    });
                }
            }
        }
    }
    public void matchEligibleUsers() {
        List<UserEntity> matchingCandidates = userRepository.findAll().stream()
                .filter(user -> "ROLE_USER".equals(user.getRole()) && user.getTeamNumber() == null)
                .collect(Collectors.toList());

        List<SurveyResponseEntity> responses = surveyResponseRepository.findAll().stream()
                .filter(res -> matchingCandidates.contains(res.getUser()))
                .collect(Collectors.toList());

        String prompt = gptService.createPrompt(responses);
        String gptResponse = gptService.callGpt(prompt);

        applyGroupingResult(gptResponse);
    }
}
