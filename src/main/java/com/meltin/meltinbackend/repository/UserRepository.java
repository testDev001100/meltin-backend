package com.meltin.meltinbackend.repository;

import com.meltin.meltinbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    //유저이름 확인
    Boolean existsByUsername(String username);

    UserEntity findByUsername(String username);


}
