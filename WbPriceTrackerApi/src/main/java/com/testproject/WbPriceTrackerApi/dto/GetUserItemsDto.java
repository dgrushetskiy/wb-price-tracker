package com.testproject.WbPriceTrackerApi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserItemsDto {

    private Long code;

    private String brand;

    private String name;

    private Integer price;
}
