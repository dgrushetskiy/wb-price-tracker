package com.testproject.WbPriceTrackerApi.integration.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserServiceIT extends IntegrationTestBase {

    private static final Long PRESENT_USER_ID = 6L;
    private static final Long NON_PRESENT_USER_ID = 100L;

    private static final String PRESENT_USERNAME = "TestUser1";
    private static final String NON_PRESENT_USERNAME = "TestUser100";

    private static final String PRESENT_EMAIL = "testUser1@gmail.com";
    private static final String NON_PRESENT_EMAIL = "testUser100@gmail.com";

    private static final String NAME = "NewTestUser";
    private static final String USERNAME = "NewTestUser";
    private static final String EMAIL = "newTestUser@gmail.com";
    private static final String PASSWORD = "password";
    private static final User user = User.builder().name(NAME).username(USERNAME).email(EMAIL).password(PASSWORD).build();

    private static final String errorMsg = "User not found";

    private final UserService userService;

    @Test
    void findAllUsersWithItems() {
        List<User> actualResult = userService.findAllUsersWithItems(Role.ROLE_USER);

        assertEquals(8, actualResult.size());
    }

    @Test
    void findById() {
        User actualResult = userService.findById(PRESENT_USER_ID);

        assertEquals(PRESENT_USERNAME, actualResult.getUsername());
    }

    @Test
    @Disabled
    void findByIdThrowsExceptionIfUserNotFound() {
        RequestException requestException = assertThrows(RequestException.class, () -> userService.findById(NON_PRESENT_USER_ID));

        assertEquals(errorMsg, requestException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
    }

    @Test
    void findByUsername() {
        Optional<User> actualResultPresent = userService.findByUsername(PRESENT_USERNAME);
        Optional<User> actualResultNonPresent = userService.findByUsername(NON_PRESENT_USERNAME);

        assertTrue(actualResultPresent.isPresent());
        assertEquals(6L, actualResultPresent.get().getId());
        assertTrue(actualResultNonPresent.isEmpty());
    }

    @Test
    void findByEmail() {
        Optional<User> actualResultPresent = userService.findByEmail(PRESENT_EMAIL);
        Optional<User> actualResultNonPresent = userService.findByEmail(NON_PRESENT_EMAIL);

        assertTrue(actualResultPresent.isPresent());
        assertEquals(6L, actualResultPresent.get().getId());
        assertTrue(actualResultNonPresent.isEmpty());
    }

    @Test
    void register() {
        userService.register(user);

        assertTrue(userService.findByUsername(USERNAME).isPresent());
        assertEquals(9, userService.findAllUsersWithItems(Role.ROLE_USER).size());
        assertEquals(10L, userService.findByUsername(USERNAME).get().getId());
        assertEquals(Role.ROLE_USER, userService.findByUsername(USERNAME).get().getRole());
    }
}
