package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserRepository userRepository;

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
}
