package com.testproject.WbPriceTrackerApi.interceptor;

import com.testproject.WbPriceTrackerApi.exception.ExceptionMessage;
import com.testproject.WbPriceTrackerApi.exception.MessageConstant;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.security.UserDetailsServiceImpl;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CheckAuthInterceptor implements HandlerInterceptor {

//    /users/{userId}/**
//    private final String URI_REGEX = "\\/users\\/\\d+\\/?.*";
    private final String URI_PREFIX = "/api/v1/users/";

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public CheckAuthInterceptor(UserService userService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userService = userService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().startsWith(URI_PREFIX)) {
            return true;
        }
        String uri = request.getRequestURI();
        String userId = uri.replace(URI_PREFIX, "").substring(0, uri.indexOf("/") + 1);

        String requiredUsername = userService.findById(Long.parseLong(userId)).getUsername();
//        Only the owner and admin have access to the user profile
        String principalUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = userDetailsServiceImpl.loadUserByUsername(principalUsername).
                getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()));
        if (!principalUsername.equals(requiredUsername) && !isAdmin) {
            log.info("{} trying to get access to {} profile", principalUsername, requiredUsername);
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.ACCESS_DENIED), HttpStatus.FORBIDDEN);
        }
        return true;
    }
}
