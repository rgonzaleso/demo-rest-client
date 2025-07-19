package com.rgonzaleso.demoalgoliaclient.service;

import com.rgonzaleso.demoalgoliaclient.entity.Hit;
import com.rgonzaleso.demoalgoliaclient.repository.AbstractPostgresIT;
import com.rgonzaleso.demoalgoliaclient.repository.HitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class HitsServiceIT extends AbstractPostgresIT {

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
