package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.JoinDTO;
import com.meltin.meltinbackend.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/api/users") //회원가입
    public ResponseEntity<JoinResponse> usersProcess(@RequestBody JoinDTO joinDTO) {
        joinService.joinProcess(joinDTO);
        return ResponseEntity.ok(new JoinResponse("회원가입 성공"));
    }
}
