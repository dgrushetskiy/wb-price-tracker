package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dto.GetUsersResponse;
import com.testproject.WbPriceTrackerApi.dto.UserForAdminDto;
import com.testproject.WbPriceTrackerApi.exception.ExceptionResponse;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin", description = "The Admin API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final DtoMapper dtoMapper;

    public AdminController(UserService userService, DtoMapper dtoMapper) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    @Operation(summary = "Get info about all users",
            description = "Received data : list of all users with info : userId, name, username, email, list of tracking items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Received information about all users",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetUsersResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Access is denied. No access permissions to the resource", content = @Content)
    })
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersInfo() {
        List<User> allUsers = userService.findAllUsersWithItems(Role.ROLE_USER);
        List<UserForAdminDto> userForAdminDtoList = allUsers.stream().map(dtoMapper::convertToUserDto).toList();
        return new ResponseEntity<>(new GetUsersResponse(userForAdminDtoList), HttpStatus.OK);
    }

    @Operation(summary = "Get information about the certain user by userId",
            description = "Received data : userId, name, username, email, list of tracking items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Received information about all users",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserForAdminDto.class)))),
            @ApiResponse(responseCode = "400", description = "User not found", content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Access is denied. No access permissions to the resource", content = @Content)
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable("userId") Long userId) {
        User user = userService.findById(userId);
        UserForAdminDto userForAdminDto = dtoMapper.convertToUserDto(user);
        return new ResponseEntity<>(userForAdminDto, HttpStatus.OK);
    }
}
