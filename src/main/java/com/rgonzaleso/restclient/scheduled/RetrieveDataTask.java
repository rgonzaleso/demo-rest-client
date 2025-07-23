package com.rgonzaleso.restclient.scheduled;

import com.rgonzaleso.restclient.service.HitsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class RetrieveDataTask {

    private final HitsService hitsService;

    @Scheduled(cron = "${cron.retrieve.data.task}")
    public void retrieveData() {
        log.info("RetrieveDataTask started at {}", LocalDateTime.now());
        hitsService.retrieveData(false);
        log.info("RetrieveDataTask completed at {}", LocalDateTime.now());
    }
}
