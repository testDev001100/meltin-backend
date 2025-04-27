package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.JoinDTO;
import com.meltin.meltinbackend.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/api/users") //회원가입
    public String usersProcess(JoinDTO joinDTO) {

        joinService.joinProcess(joinDTO);

        return "Users processed";
    }
}
