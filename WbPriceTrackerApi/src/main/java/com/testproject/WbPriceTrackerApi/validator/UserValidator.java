package com.testproject.WbPriceTrackerApi.validator;

import com.testproject.WbPriceTrackerApi.exception.ExceptionMessage;
import com.testproject.WbPriceTrackerApi.exception.MessageConstant;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            log.info("Fail while register new user. Username {} already in use", user.getUsername());

            errors.rejectValue("username", "",
                    ExceptionMessage.setMessage(MessageConstant.USERNAME_ALREADY_USED, user.getUsername()));
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            log.info("Fail while register new user. Email {} already in use", user.getEmail());

            errors.rejectValue("email", "",
                    ExceptionMessage.setMessage(MessageConstant.EMAIL_ALREADY_USED, user.getEmail()));
        }
    }
}
