package com.rgonzaleso.restclient.controller;

import com.rgonzaleso.restclient.dto.HideHitRequestDTO;
import com.rgonzaleso.restclient.dto.HitDTO;
import com.rgonzaleso.restclient.dto.HitUserRequestDTO;
import com.rgonzaleso.restclient.service.HitsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.rgonzaleso.restclient.constants.AppConstants.MAX_SIZE_PER_PAGE;
import static com.rgonzaleso.restclient.constants.AppConstants.SORT_BY_ID;

@RestController
@RequiredArgsConstructor
public class HitController {

    public static final String HIT_PATH = "/api/v1/hits";
    public static final String HIT_PATH_SEED = HIT_PATH + "/seed";
    public static final String HIT_PATH_FIND = HIT_PATH + "/find";
    public static final String HIT_PATH_HIDE = HIT_PATH + "/hide";

    private final HitsService hitsService;

    @GetMapping(HIT_PATH_SEED)
    public void seed(){
        hitsService.retrieveData(true);
    }

    @GetMapping(HIT_PATH_FIND)
    public Page<HitDTO> find(
            @Valid @ModelAttribute HitUserRequestDTO filter,
            @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, MAX_SIZE_PER_PAGE, Sort.by(SORT_BY_ID));
        return hitsService.findByCriteria(filter, pageable);
    }

    @PostMapping(HIT_PATH_HIDE)
    public void hide(@Valid @RequestBody HideHitRequestDTO dto){
        hitsService.hideHits(dto.getIds());
    }
}
