package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dao.ItemDao;
import com.testproject.WbPriceTrackerApi.dto.*;
import com.testproject.WbPriceTrackerApi.exception.ExceptionResponse;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.service.PriceService;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import com.testproject.WbPriceTrackerApi.validator.ItemValidator;
import com.testproject.WbPriceTrackerApi.validator.ValidationErrMsgBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "User", description = "The User API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final PriceService priceService;
    private final ItemDao itemDao;
    private final ItemService itemService;
    private final DtoMapper dtoMapper;
    private final ItemValidator itemValidator;

    @Autowired
    public UserController(UserService userService, PriceService priceService, ItemDao itemDao, ItemService itemService, DtoMapper dtoMapper,
                          ItemValidator itemValidator) {
        this.userService = userService;
        this.priceService = priceService;
        this.itemDao = itemDao;
        this.itemService = itemService;
        this.dtoMapper = dtoMapper;
        this.itemValidator = itemValidator;
    }

    @Operation(summary = "Get info about all user items by userId", description = "Received data : code, name, brand, last updated price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully received list of user items",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetUserItemsResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "User trying to get access to another user profile",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class))))
    })
    @GetMapping("/{userId}/items")
    public ResponseEntity<?> getUserItems(@PathVariable("userId") Long userId) {
        // CheckAuthInterceptor -> preHandle() -> checks if the user is trying to get item from their own profile

        User user = userService.findById(userId);
        List<GetUserItemsDto> items = itemDao.findAllUsersItems(user);
        return new ResponseEntity<>(new GetUserItemsResponse(user.getUsername(), items), HttpStatus.OK);

    }


    @Operation(summary = "Add tracking item to user profile by userId", description = "Required data : item code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added item to the user profile",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Fail while adding item to profile : incorrect userId or item code",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "User trying to get access to another user profile",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class))))
    })
    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addItemToProfile(@PathVariable("userId") Long userId, @RequestBody @Valid ItemCodeDto itemCodeDto,
                                              BindingResult bindingResult) {
        // CheckAuthInterceptor -> preHandle() -> checks if the user is trying to add item at their own profile

        Item item = dtoMapper.convertToItem(itemCodeDto);
        //check if the code is in the db
        //if present, add the code only to the user profile; if not, add the code to the db & profile
        if (itemService.findByCode(item.getCode()).isEmpty()) {
            if (item.getCode() >= 2_000_000) itemValidator.validate(item, bindingResult);
            if (bindingResult.hasErrors()) {
                String errMsg = ValidationErrMsgBuilder.buildAllErrMsg(bindingResult);
                throw new RequestException(errMsg, HttpStatus.BAD_REQUEST);
            }
        }
        itemService.addItemToProfile(userService.findById(userId), item);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "Get tracking info about current item prices by userId and item code",
            description = "It is possible to get price values for the entire tracking period " +
            "or specify a time period in the parameters : fromDate \\ toDate in format \"yyyy-MM-dd\". \n" +
                    "Received data : item code, list of prices and dates for the selected period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully received list of item prices",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GetItemPricesResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Fail while getting item prices from the user profile : incorrect userId or item code",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "User trying to get access to another user profile",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class))))
    })
    @GetMapping("/{userId}/items/{itemCode}")
    public ResponseEntity<?> getItemPricesTrackingInfo(@PathVariable("userId") Long userId,
                                                       @PathVariable("itemCode") Long code,
                                                       @RequestParam(value = "fromDate", required = false)
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                                       @RequestParam(value = "toDate", required = false)
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        // CheckAuthInterceptor -> preHandle() -> checks if the user is trying to get tracking info from their own profile

        User user = userService.findById(userId);
        PriceFilter priceFilter = PriceFilter.builder()
                .fromDate(fromDate == null ? null : fromDate.atStartOfDay())
                .toDate(toDate == null ? null : toDate.atStartOfDay().plusDays(1L))
                .build();
        List<GetItemPricesDto> allItemPrices = priceService.findAllItemPrices(user, code, priceFilter);
        return new ResponseEntity<>(new GetItemPricesResponse(code, allItemPrices), HttpStatus.OK);
    }


    @Operation(summary = "Delete item from user profile by userId and item code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted item from the user profile",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Fail while deleting item from user profile : incorrect userId or item code",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "User trying to get access to another user profile",
                    content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExceptionResponse.class))))
    })
    @DeleteMapping("/{userId}/items/{itemCode}")
    public ResponseEntity<?> deleteItemFromProfile(@PathVariable("userId") Long userId, @PathVariable("itemCode") Long code) {
        // CheckAuthInterceptor -> preHandle() -> checks if the user is trying to delete item from their own profile

        itemService.deleteItemFromProfile(userService.findById(userId), code);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
