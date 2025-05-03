package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.dto.PasswordUpdateDTO;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void updatePassword(String username, PasswordUpdateDTO dto) {
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(),user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        String newEncodedPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(newEncodedPassword);
        userRepository.save(user);
    }
    @Transactional
    public void deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        userRepository.delete(user);
    }
}
