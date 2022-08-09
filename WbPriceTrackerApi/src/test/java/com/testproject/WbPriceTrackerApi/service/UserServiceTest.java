package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final User user1 = User.builder().id(1L).name("TestUser1").username("TestUser1").email("testUser1@gmail.com")
            .password("password1").role(Role.ROLE_USER).build();
    private static final User user2 = User.builder().id(2L).name("TestUser2").username("TestUser2").email("testUser2@gmail.com")
            .password("password2").role(Role.ROLE_USER).build();
    private static final User user3 = User.builder().id(3L).name("TestUser3").username("TestUser3").email("testUser3@gmail.com")
            .password("password3").build();

    private static final List<User> usersList = List.of(user1, user2);

    private static final Long USER_ID = 1L;
    private static final String USER_USERNAME = "TestUser1";
    private static final String USER_EMAIL = "testUser1@gmail.com";

    private static final String errorMsg = "User not found";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void findAllUsersWithItems() {
        doReturn(usersList).when(userRepository).findByRole(Role.ROLE_USER);
        List<User> actualResult = userService.findAllUsersWithItems(Role.ROLE_USER);

        assertEquals(usersList, actualResult);
        verify(userRepository, times(1)).findByRole(Role.ROLE_USER);
    }

    @Test
    void findById() {
        doReturn(Optional.of(user1)).when(userRepository).findById(USER_ID);
        User actualResult = userService.findById(USER_ID);

        assertEquals(user1, actualResult);
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findByIdThrowsExceptionIfUserNotFound() {
        doReturn(Optional.empty()).when(userRepository).findById(USER_ID);
        RequestException requestException = assertThrows(RequestException.class, () -> userService.findById(USER_ID));

        assertEquals(errorMsg, requestException.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findByUsername() {
        doReturn(Optional.of(user1)).when(userRepository).findByUsername(USER_USERNAME);
        Optional<User> actualResult = userService.findByUsername(USER_USERNAME);

        assertTrue(actualResult.isPresent());
        assertEquals(user1, actualResult.get());
        verify(userRepository, times(1)).findByUsername(USER_USERNAME);
    }

    @Test
    void findByEmail() {
        doReturn(Optional.of(user1)).when(userRepository).findByEmail(USER_EMAIL);
        Optional<User> actualResult = userService.findByEmail(USER_EMAIL);

        assertTrue(actualResult.isPresent());
        assertEquals(user1, actualResult.get());
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void register() {
        userService.register(user3);

        assertEquals(Role.ROLE_USER, user3.getRole());
        verify(userRepository, times(1)).save(user3);
    }
}