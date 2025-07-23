package com.rgonzaleso.restclient.service;


import com.rgonzaleso.restclient.dto.HitDTO;
import com.rgonzaleso.restclient.dto.HitUserRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HitsService {

    void retrieveData(boolean firstLoad);

    Page<HitDTO> findByCriteria(HitUserRequestDTO filters, Pageable pageable);

    void hideHits(List<Long> ids);
}
