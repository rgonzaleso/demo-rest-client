package com.rgonzaleso.restclient.repository;

import com.rgonzaleso.restclient.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestContainerConfiguration.class)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsername() {
        Optional<UserEntity> user = userRepository.findByUsername("rgonzaleso");
        assertTrue(user.isPresent());
    }
}