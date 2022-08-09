package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dto.UserForAdminDto;
import com.testproject.WbPriceTrackerApi.interceptor.CheckAuthInterceptor;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.security.JwtRequestAuthenticationFilter;
import com.testproject.WbPriceTrackerApi.security.JwtUtil;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    private static final String requestMapping = "/api/v1/admin";

    private static final Long USER1_ID = 1L;
    private static final String USERNAME1 = "TestUser1";
    private static final User user1 = User.builder().id(USER1_ID).name(USERNAME1).username(USERNAME1).email("testUser1@gmail.com")
            .password("password").role(Role.ROLE_USER).build();
    private static final UserForAdminDto user1Dto = UserForAdminDto.builder().id(USER1_ID).name(USERNAME1).username(USERNAME1).email("testUser1@gmail.com").build();

    private static final Long USER2_ID = 2L;
    private static final String USERNAME2 = "TestUser2";
    private static final User user2 = User.builder().id(USER2_ID).name(USERNAME2).username(USERNAME2).email("testUser2@gmail.com")
            .password("password").role(Role.ROLE_USER).build();
    private static final UserForAdminDto user2Dto = UserForAdminDto.builder().id(USER2_ID).name(USERNAME2).username(USERNAME2).email("testUser2@gmail.com").build();

    List<User> allUsers = List.of(user1, user2);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private DtoMapper dtoMapper;
    @MockBean
    CheckAuthInterceptor checkAuthInterceptor;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtRequestAuthenticationFilter jwtRequestAuthenticationFilter;

    @SneakyThrows
    @Test
    void getAllUsersInfo() {
        doReturn(allUsers).when(userService).findAllUsersWithItems(Role.ROLE_USER);
        doReturn(user1Dto).when(dtoMapper).convertToUserDto(allUsers.get(0));
        doReturn(user2Dto).when(dtoMapper).convertToUserDto(allUsers.get(1));

        mockMvc.perform(get(requestMapping + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.users[0].username").value(USERNAME1))
                .andExpect(jsonPath("$.users[1].username").value(USERNAME2))
                .andDo(print());
        verify(userService, times(1)).findAllUsersWithItems(Role.ROLE_USER);
        verify(dtoMapper, times(2)).convertToUserDto(any(User.class));
    }

    @SneakyThrows
    @Test
    void getUserProfile() {
        doReturn(user1).when(userService).findById(USER1_ID);
        doReturn(user1Dto).when(dtoMapper).convertToUserDto(user1);

        mockMvc.perform(get(requestMapping + "/users/{userId}", USER1_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(USER1_ID))
                .andExpect(jsonPath("$.username").value(USERNAME1))
                .andDo(print());
        verify(userService, times(1)).findById(USER1_ID);
        verify(dtoMapper, times(1)).convertToUserDto(any(User.class));
    }
}