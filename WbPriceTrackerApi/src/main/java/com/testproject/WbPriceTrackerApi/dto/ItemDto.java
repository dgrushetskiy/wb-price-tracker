package com.testproject.WbPriceTrackerApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemDto {

    private Long code;

    private String brand;

    private String name;
}
