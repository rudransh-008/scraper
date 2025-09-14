package com.scraper.controller;

import com.scraper.model.ScrapeRequest;
import com.scraper.model.ScrapeResponse;
import com.scraper.service.WebScrapingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScrapingController.class)
class ScrapingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebScrapingService webScrapingService;

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/scrape/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testServiceInfo() throws Exception {
        mockMvc.perform(get("/scrape/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Web Scraper Service"));
    }

    @Test
    void testScrapeWebData() throws Exception {
        ScrapeResponse mockResponse = ScrapeResponse.builder()
                .searchTopic("test topic")
                .totalResults(1)
                .successfulScrapes(1)
                .failedScrapes(0)
                .results(Collections.emptyList())
                .status("completed")
                .message("Success")
                .build();

        when(webScrapingService.scrapeWebData(any(ScrapeRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/scrape/web")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"searchTopic\":\"test topic\",\"maxResults\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchTopic").value("test topic"))
                .andExpect(jsonPath("$.status").value("completed"));
    }
}
