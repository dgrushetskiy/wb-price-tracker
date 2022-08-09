package com.testproject.WbPriceTrackerApi.integration.controller;

import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
class ParserControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void getAllItemsCodes() {

        mockMvc.perform(get("/api/v1/parser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.codes[10].code").value(15061497))
                .andExpect(jsonPath("$.codes[19].code").value(12052239))
                .andExpect(jsonPath("$.codes.length()").value(20))
                .andDo(print());
    }
}