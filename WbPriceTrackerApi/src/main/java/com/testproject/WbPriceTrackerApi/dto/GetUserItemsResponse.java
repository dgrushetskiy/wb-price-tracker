package com.testproject.WbPriceTrackerApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserItemsResponse {

    private String username;

    private List<GetUserItemsDto> items;
}
