package com.rgonzaleso.demoalgoliaclient.service;


import com.rgonzaleso.demoalgoliaclient.dto.HitDTO;
import com.rgonzaleso.demoalgoliaclient.dto.HitUserRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HitsService {

    void retrieveData(boolean firstLoad);

    Page<HitDTO> findByCriteria(HitUserRequestDTO filters, Pageable pageable);

    void hideHits(List<Long> ids);
}
