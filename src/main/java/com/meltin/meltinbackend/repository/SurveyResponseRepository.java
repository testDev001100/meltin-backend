package com.meltin.meltinbackend.repository;

import com.meltin.meltinbackend.entity.SurveyResponseEntity;
import com.meltin.meltinbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponseEntity, Long> {
    boolean existsByUserId(Long userId);

    Optional<Object> findByUser(UserEntity user);
}