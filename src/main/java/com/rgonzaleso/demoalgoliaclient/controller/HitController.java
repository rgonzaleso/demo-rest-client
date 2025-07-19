package com.rgonzaleso.demoalgoliaclient.controller;

import com.rgonzaleso.demoalgoliaclient.dto.HideHitRequestDTO;
import com.rgonzaleso.demoalgoliaclient.dto.HitDTO;
import com.rgonzaleso.demoalgoliaclient.dto.HitUserRequestDTO;
import com.rgonzaleso.demoalgoliaclient.service.HitsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import static com.rgonzaleso.demoalgoliaclient.constants.AppConstants.MAX_SIZE_PER_PAGE;
import static com.rgonzaleso.demoalgoliaclient.constants.AppConstants.SORT_BY_ID;

@RestController
@RequestMapping("/api/v1/hits")
@AllArgsConstructor
public class HitController {

    private final HitsService hitsService;

    @GetMapping("/seed")
    public void seed(){
        hitsService.retrieveData(true);
    }

    @GetMapping("/find")
    public Page<HitDTO> find(
            @Valid @ModelAttribute HitUserRequestDTO filter,
            @RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, MAX_SIZE_PER_PAGE, Sort.by(SORT_BY_ID));
        return hitsService.findByCriteria(filter, pageable);
    }

    @PostMapping("/hide")
    public void hide(@Valid @RequestBody HideHitRequestDTO dto){
        hitsService.hideHits(dto.getIds());
    }
}
