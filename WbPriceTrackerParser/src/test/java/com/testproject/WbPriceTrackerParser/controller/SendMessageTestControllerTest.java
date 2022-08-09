package com.testproject.WbPriceTrackerParser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SendMessageTestController.class)
@AutoConfigureMockMvc
class SendMessageTestControllerTest {

    private static final Long ITEM_CODE1 = 15061503L;
    private static final Integer CODE1_PRICE = 1176;
    private static final PriceDto jsonData = PriceDto.builder().code(ITEM_CODE1).price(CODE1_PRICE).build();
    private static final String responseMsg = "Message sent successfully";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private SendMessageTestController sendMessageTestController;

    @SneakyThrows
    @Test
    void sendPrice() {
        MvcResult actualResult = mockMvc.perform(post("/test/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertEquals(responseMsg, actualResult.getResponse().getContentAsString());
    }
}