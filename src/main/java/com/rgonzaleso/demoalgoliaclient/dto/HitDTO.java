package com.rgonzaleso.demoalgoliaclient.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record HitDTO(Long id, JsonNode hit) {
}
