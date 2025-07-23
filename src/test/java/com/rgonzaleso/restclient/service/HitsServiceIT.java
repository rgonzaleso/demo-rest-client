package com.rgonzaleso.restclient.service;

import com.rgonzaleso.restclient.entity.Hit;
import com.rgonzaleso.restclient.repository.TestContainerConfiguration;
import com.rgonzaleso.restclient.repository.HitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainerConfiguration.class)
class HitsServiceIT  {

    @Autowired
    HitsService hitsService;

    @Autowired
    HitRepository hitRepository;

    @Test
    void hideHits(){
        Hit hit1 = Hit.builder()
                .hidden(false)
                .author("Rolando")
                .month("september")
                .title("test backend java")
                .createdDate(LocalDateTime.now())
                .build();

        hitRepository.save(hit1);

        assertThat(hit1.getId()).isNotNull();
        assertThat(hit1.getHidden()).isFalse();

        hitsService.hideHits(List.of(hit1.getId()));

        Hit hidden =  hitRepository.findById(hit1.getId()).get();
        assertThat(hidden.getHidden()).isTrue();
    }
}
