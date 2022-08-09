package com.testproject.WbPriceTrackerApi.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerApi.dto.AuthDto;
import com.testproject.WbPriceTrackerApi.dto.RegisterDto;
import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
class AuthControllerIT extends IntegrationTestBase {

    //    TestUser1(6, "TestUser1", "TestUser1", "testUser1@gmail.com","password", 'ROLE_USER');
    private final static String EXISTING_USERNAME = "TestUser1";
    private final static String EXISTING_EMAIL = "testUser1@gmail.com";
    private final static String CORRECT_PASSWORD = "password";
    private static final AuthDto authDtoCorrect = AuthDto.builder().username(EXISTING_USERNAME).password(CORRECT_PASSWORD).build();

    private static final String NAME = "TestUser10";
    private static final String USERNAME = "TestUser10";
    private static final String EMAIL = "testUser10@gmail.com";
    private static final String PASSWORD = "password";
    private static final RegisterDto registerDto = RegisterDto.builder().name(NAME).username(USERNAME).email(EMAIL).password(PASSWORD).build();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final UserService userService;

    @SneakyThrows
    @Test
    void performRegistrationSuccessful() {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        assertTrue(userService.findByUsername(USERNAME).isPresent());
        assertEquals(10, userService.findByUsername(USERNAME).get().getId());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("com.testproject.WbPriceTrackerApi.integration.controller.AuthControllerIT#getArgumentsForRegistrationUnsuccessfulTest")
    void performRegistrationUnsuccessful(String name, String username, String email, String password, String errMsg) {
        MvcResult actualResult = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString
                                (RegisterDto.builder().name(name).username(username).email(email).password(password).build())))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        if (!username.equals(EXISTING_USERNAME)) assertFalse(userService.findByUsername(username).isPresent());
        assertTrue(actualResult.getResponse().getContentAsString().contains(errMsg));
    }

    @SneakyThrows
    @Test
    void performLoginSuccessful() {
        MvcResult actualResponse = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDtoCorrect)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(header().exists("Authorization"))
                .andDo(print())
                .andReturn();
        assertTrue(actualResponse.getResponse().getHeader("Authorization").startsWith("Bearer "));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("com.testproject.WbPriceTrackerApi.integration.controller.AuthControllerIT#getArgumentsForLoginUnsuccessfulTest")
    void performLoginUnsuccessful(String username, String password) {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString
                                (AuthDto.builder().username(username).password(password).build())))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    static Stream<Arguments> getArgumentsForLoginUnsuccessfulTest() {
        final String AUTH_INCORRECT_USERNAME = "dummy";
        final String AUTH_INCORRECT_PASSWORD = "dummy";

        return Stream.of(
                Arguments.of(AUTH_INCORRECT_USERNAME, CORRECT_PASSWORD),
                Arguments.of(EXISTING_USERNAME, AUTH_INCORRECT_PASSWORD),
                Arguments.of(AUTH_INCORRECT_USERNAME, AUTH_INCORRECT_PASSWORD)
        );
    }

    static Stream<Arguments> getArgumentsForRegistrationUnsuccessfulTest() {
        final String INCORRECT_NAME = "f";
        final String INCORRECT_USERNAME = "f";
        final String errMsgForIncorrectNameAndUsername = "The field must contain from 2 to 128 characters";
        final String INCORRECT_EMAIL = "dummy@dummy";
        final String errMsgForIncorrectEmail = "Incorrect email";
        final String INCORRECT_PASSWORD = "pass";
        final String errMsgForIncorrectPassword = "Password must contain at least 5 characters";
        final String errMsgAlreadyRegisterUsernameOrEmail = "already in use";

        return Stream.of(
                Arguments.of(INCORRECT_NAME, USERNAME, EMAIL, PASSWORD, errMsgForIncorrectNameAndUsername),
                Arguments.of(NAME, INCORRECT_USERNAME, EMAIL, PASSWORD, errMsgForIncorrectNameAndUsername),
                Arguments.of(NAME, USERNAME, INCORRECT_EMAIL, PASSWORD, errMsgForIncorrectEmail),
                Arguments.of(NAME, USERNAME, EMAIL, INCORRECT_PASSWORD, errMsgForIncorrectPassword),
                Arguments.of(NAME, EXISTING_USERNAME, EMAIL, PASSWORD, errMsgAlreadyRegisterUsernameOrEmail),
                Arguments.of(NAME, USERNAME, EXISTING_EMAIL, PASSWORD, errMsgAlreadyRegisterUsernameOrEmail)
                );
    }
}