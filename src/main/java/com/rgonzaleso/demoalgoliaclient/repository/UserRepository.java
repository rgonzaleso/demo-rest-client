package com.rgonzaleso.demoalgoliaclient.repository;

import com.rgonzaleso.demoalgoliaclient.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
