package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dto.AuthDto;
import com.testproject.WbPriceTrackerApi.dto.RegisterDto;
import com.testproject.WbPriceTrackerApi.exception.ExceptionResponse;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import com.testproject.WbPriceTrackerApi.validator.UserValidator;
import com.testproject.WbPriceTrackerApi.validator.ValidationErrMsgBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Auth", description = "Authentication API")
@RestController
public class AuthController {

    private final DtoMapper dtoMapper;
    private final UserValidator userValidator;
    private final UserService userService;

    @Autowired
    public AuthController(DtoMapper dtoMapper, UserValidator userValidator, UserService userService) {
        this.dtoMapper = dtoMapper;
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @Operation(summary = "Register new user", description = "Required data : name, unique username, unique email, password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user successfully registered",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Fail while register new user : incorrect input data",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class))))
    })
    @PostMapping("/register")
    public ResponseEntity<?> performRegistration(@RequestBody @Valid RegisterDto registerDto,
                                                          BindingResult bindingResult) {
        User user = dtoMapper.convertToUser(registerDto);
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            String errMsg = ValidationErrMsgBuilder.buildFieldErrMsg(bindingResult);
            throw new RequestException(errMsg, HttpStatus.BAD_REQUEST);
        }
        userService.register(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Login user profile",
            description = "In response you will get JWT token in Authentication HTTP header with \"Bearer \" prefix")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Login user profile, received JWT token in Authentication HTTP header",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized : authentication required", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> performLogin(@RequestBody AuthDto AuthDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
