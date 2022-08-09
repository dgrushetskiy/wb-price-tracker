package com.testproject.WbPriceTrackerParser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PriceDto implements Serializable {

    @JsonProperty("code")
    private Long code;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("date")
    private String date;
}
