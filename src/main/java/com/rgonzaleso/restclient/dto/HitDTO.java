package com.rgonzaleso.restclient.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record HitDTO(Long id, JsonNode hit) {
}
