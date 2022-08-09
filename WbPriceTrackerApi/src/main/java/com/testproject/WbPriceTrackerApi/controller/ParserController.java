package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dto.ParserResponse;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Tag(name = "Parser", description = "Parser API")
@RestController
@RequestMapping("/api/v1/parser")
public class ParserController {

    private final ItemService itemService;
    private final DtoMapper dtoMapper;

    public ParserController(ItemService itemService, DtoMapper dtoMapper) {
        this.itemService = itemService;
        this.dtoMapper = dtoMapper;
    }

    @Operation(summary = "Get all items codes from Db to update prices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Received all items codes from Db to update prices",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ParserResponse.class))))
    })
    @GetMapping()
    public ResponseEntity<?> getAllItemsCodes() {
        return new ResponseEntity<>(new ParserResponse(
                itemService.findAll()
                        .stream()
                        .map(dtoMapper::convertToItemDto)
                        .collect(Collectors.toList())), HttpStatus.OK);
    }
}

//prev version with POST request to Api with updated prices instead of using RabbitMQ

//    @PostMapping()
//    public ResponseEntity<?> addPriceFromParser(@RequestBody @Valid PriceDto priceDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            String errMsg = ValidationErrMsgBuilder.buildFieldErrMsg(bindingResult);
//            throw new RequestException(errMsg, HttpStatus.BAD_REQUEST);
//        }
//        Price price = dtoMapper.convertToPrice(priceDto);
//        priceService.addPriceFromParser(price);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
