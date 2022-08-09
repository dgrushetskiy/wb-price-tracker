package com.testproject.WbPriceTrackerApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetItemPricesResponse {

    private Long code;

    private List<GetItemPricesDto> prices;
}
