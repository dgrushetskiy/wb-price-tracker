package com.testproject.WbPriceTrackerApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerApi.dto.AuthDto;
import com.testproject.WbPriceTrackerApi.dto.RegisterDto;
import com.testproject.WbPriceTrackerApi.interceptor.CheckAuthInterceptor;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.security.JwtRequestAuthenticationFilter;
import com.testproject.WbPriceTrackerApi.security.JwtUtil;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import com.testproject.WbPriceTrackerApi.validator.UserValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final Long USER_ID = 1L;
    private static final String NAME = "TestUser1";
    private static final String USERNAME = "TestUser1";
    private static final String EMAIL = "testUser1@gmail.com";
    private static final String PASSWORD = "password";
    private static final RegisterDto registerDto = RegisterDto.builder().name(NAME).username(USERNAME).email(EMAIL).password(PASSWORD).build();
    private static final AuthDto authDto = AuthDto.builder().username(USERNAME).password(PASSWORD).build();
    private static final User user = User.builder().id(USER_ID).name(NAME).username(USERNAME).email(EMAIL)
            .password(PASSWORD).role(Role.ROLE_USER).build();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DtoMapper dtoMapper;
    @MockBean
    private UserValidator userValidator;
    @MockBean
    private UserService userService;
    @MockBean
    CheckAuthInterceptor checkAuthInterceptor;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtRequestAuthenticationFilter jwtRequestAuthenticationFilter;

    @SneakyThrows
    @Test
    void performRegistration() {
        doReturn(user).when(dtoMapper).convertToUser(any(RegisterDto.class));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        verify(dtoMapper, times(1)).convertToUser(any(RegisterDto.class));
        verify(userValidator, times(1)).validate(any(User.class), any(BindingResult.class));
        verify(userService, times(1)).register(user);
    }

    @SneakyThrows
    @Test
    void performLogin() {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDto)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
}