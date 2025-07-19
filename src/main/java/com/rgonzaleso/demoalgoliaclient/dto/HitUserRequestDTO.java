package com.rgonzaleso.demoalgoliaclient.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HitUserRequestDTO {

    @Pattern(regexp = "january|february|march|april|may|june|july|august|september|october|november|december", message = "Invalid month")
    private String month;
    private String author;
    private String title;
    private List<String> tags;

}
