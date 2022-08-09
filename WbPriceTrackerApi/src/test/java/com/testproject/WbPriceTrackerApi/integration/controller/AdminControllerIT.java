package com.testproject.WbPriceTrackerApi.integration.controller;

import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
class AdminControllerIT extends IntegrationTestBase {

    private static final String requestMapping = "/api/v1/admin";

    private static final String forbiddenErrMsg = "You do not have permissions to access the resource";

    //    Admin(1, 'AdminProfile', 'AdminProfile', 'admin@gmail.com','adminPassword', 'ROLE_ADMIN');
    private final static String ADMIN_USERNAME = "AdminProfile";
    //    TestUser1(6, "TestUser1", "TestUser1", "testUser1@gmail.com","password", 'ROLE_USER');
    private final static Long USER6_ID = 6L;
    private final static String USER6_USERNAME = "TestUser1";

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void getAllUsersInfo() {
        mockMvc.perform(get(requestMapping + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.users.length()").value(8))
                .andExpect(jsonPath("$.users[4].username").value(USER6_USERNAME))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void getAllUsersInfoForbiddenForRoleUser() {
        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(forbiddenErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void getUserProfile() {
        mockMvc.perform(get(requestMapping + "/users/{userId}", USER6_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(USER6_ID))
                .andExpect(jsonPath("$.username").value(USER6_USERNAME))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void getUserProfileForbiddenForRoleUser() {
        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/users/{userId}", USER6_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(forbiddenErrMsg));
    }
}