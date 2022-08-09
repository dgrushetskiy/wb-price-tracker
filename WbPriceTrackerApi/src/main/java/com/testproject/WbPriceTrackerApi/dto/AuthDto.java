package com.testproject.WbPriceTrackerApi.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthDto {

    @NotBlank(message = "The field must not be empty")
    @Size(min = 2, max = 128, message = "The field must contain from 2 to 128 characters")
    private String username;

    @NotEmpty(message = "The field must not be empty")
    @Size(min = 5, message = "Password must contain at least 5 characters")
    private String password;
}
