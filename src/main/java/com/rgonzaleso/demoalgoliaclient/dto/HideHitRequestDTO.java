package com.rgonzaleso.demoalgoliaclient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class HideHitRequestDTO {

    @NotNull(message = "Must not be empty")
    @Size(min = 1, message = "Must provide at least 1 element")
    private List<Long> ids;

}
