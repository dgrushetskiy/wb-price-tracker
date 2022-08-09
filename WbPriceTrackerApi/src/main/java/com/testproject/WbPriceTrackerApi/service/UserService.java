package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsersWithItems(Role role) {
        return userRepository.findByRole(role);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.info("Fail while finding user with id {}", id);
            throw new RequestException("User not found", HttpStatus.BAD_REQUEST);
        });
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        log.info("New user has been registered. Name : {}, Username : {}, Email: {}",
                user.getName(), user.getUsername(), user.getEmail());
    }
}
