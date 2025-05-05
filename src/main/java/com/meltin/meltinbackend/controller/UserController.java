package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.dto.MyPageDTO;
import com.meltin.meltinbackend.dto.PasswordUpdateDTO;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.UserRepository;
import com.meltin.meltinbackend.service.UserService;
import com.meltin.meltinbackend.service.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PatchMapping("/api/users/password")
    public String updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestBody PasswordUpdateDTO dto) {

        userService.updatePassword(userDetails.getUsername(),dto);
        return "비밀번호 변경 성공";
    }

    @GetMapping("/api/users/me")
    public MyPageDTO getMyUsers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();
        UserEntity user = userRepository.findByUsername(username);

        return  new MyPageDTO(
                user.getUsername(),
                user.getStudentId(),
                user.getName()
        );
    }

    @DeleteMapping("/api/users/me")
    public String deleteMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();
        userService.deleteUser(username);
        return "삭제완료";
    }
}
