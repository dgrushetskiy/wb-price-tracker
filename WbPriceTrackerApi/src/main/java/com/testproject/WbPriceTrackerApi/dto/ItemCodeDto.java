package com.testproject.WbPriceTrackerApi.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemCodeDto {

    @NotNull(message = "The field must not be empty")
    @Min(value = 2_000_000L, message = "Incorrect code. Item with this code doesn't exist")
    private Long code;
}
