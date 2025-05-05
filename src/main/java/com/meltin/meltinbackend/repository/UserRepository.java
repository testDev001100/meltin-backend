package com.meltin.meltinbackend.repository;

import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    //유저이름 확인
    Boolean existsByUsername(String username);

    UserEntity findByUsername(String username);

    void deleteByUsername(String username);

    Optional<UserEntity> findByName(String name);

    List<UserEntity> findAllByTeamNumber(Integer teamNumber);


}
