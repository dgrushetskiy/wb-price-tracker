package com.testproject.WbPriceTrackerApi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForAdminDto {

    private Long id;

    private String name;

    private String username;

    private String email;

    private List<ItemDto> items = new ArrayList<>();
}
