package com.rgonzaleso.restclient.repository;

import com.rgonzaleso.restclient.entity.Hit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestContainerConfiguration.class)
class HitRepositoryTest {

    @Autowired
    HitRepository hitRepository;

    @Test
    @Rollback
    void saveHit(){
        Hit hit = Hit.builder()
                .storyId(1L)
                .author("rgonzaleso")
                .build();
        hitRepository.save(hit);

        assertThat(hit.getId()).isNotNull();
    }


}