package com.meltin.meltinbackend.service;

import com.meltin.meltinbackend.dto.JoinDTO;
import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExists = userRepository.existsByUsername(username);

        if (isExists) {

            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        //회원정보 저장
        UserEntity data = new UserEntity();
        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setStudentId(joinDTO.getStudentId());
        data.setName(joinDTO.getName());
        if ("admin".equals(username)) {
            data.setRole("ROLE_ADMIN");
        } else {
            data.setRole("ROLE_USER");
        }
        userRepository.save(data);
    }
}
