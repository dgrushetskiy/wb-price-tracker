package com.testproject.WbPriceTrackerApi.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterDto {

    @NotBlank(message = "The field must not be empty")
    @Size(min = 2, max = 128, message = "The field must contain from 2 to 128 characters")
    private String name;

    @NotBlank(message = "The field must not be empty")
    @Size(min = 2, max = 128, message = "The field must contain from 2 to 128 characters")
    private String username;

    @Pattern(regexp = "^.+@.+(\\.[^.]+)+$", message = "Incorrect email. Email pattern : xx@xx.xx")
    private String email;

    @NotEmpty(message = "The field must not be empty")
    @Size(min = 5, message = "Password must contain at least 5 characters")
    private String password;
}
