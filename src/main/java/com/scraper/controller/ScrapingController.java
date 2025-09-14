package com.scraper.controller;

import com.scraper.model.ScrapeRequest;
import com.scraper.model.ScrapeResponse;
import com.scraper.service.WebScrapingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/scrape")
@CrossOrigin(origins = "*")
@Tag(name = "Web Scraping", description = "APIs for web scraping operations")
public class ScrapingController {

    private static final Logger log = LoggerFactory.getLogger(ScrapingController.class);
    
    @Autowired
    private WebScrapingService webScrapingService;

    @Operation(
        summary = "Scrape web data",
        description = "Scrapes web data from multiple URLs based on a search topic. Returns extracted information including emails, phone numbers, social links, and content."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Scraping completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScrapeResponse.class),
                examples = @ExampleObject(
                    name = "Successful Response",
                    value = """
                    {
                        "searchTopic": "software development companies",
                        "maxResults": 5,
                        "fieldsToExtract": ["emails", "phoneNumbers", "socialLinks"],
                        "exportAsCsv": false,
                        "searchEngine": "google",
                        "language": "en",
                        "country": "us"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "200",
            description = "CSV file download (when exportAsCsv=true)",
            content = @Content(
                mediaType = "application/octet-stream",
                examples = @ExampleObject(
                    name = "CSV Export",
                    value = "URL,Status,Response Time (ms),Emails,Phone Numbers,Social Links\nhttps://example.com,success,1500,contact@example.com;info@example.com,+1-555-123-4567,https://linkedin.com/company/example"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid input parameters",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/web", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> scrapeWebData(
        @Parameter(
            description = "Scraping request containing search topic and parameters",
            required = true,
            schema = @Schema(implementation = ScrapeRequest.class)
        )
        @Valid @RequestBody ScrapeRequest request) {
        log.info("Received scraping request for topic: {}", request.getSearchTopic());
        
        try {
            ScrapeResponse response = webScrapingService.scrapeWebData(request);
            
            // If CSV export is requested, return CSV file
            if (request.getExportAsCsv() != null && request.getExportAsCsv()) {
                String csvContent = webScrapingService.convertToCsv(response.getResults(), request.getFieldsToExtract());
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", 
                    "scraped_data_" + System.currentTimeMillis() + ".csv");
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
            }
            
            // Return JSON response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.ok()
                .headers(headers)
                .body(response);
        } catch (Exception e) {
            log.error("Error processing scraping request: ", e);
            return ResponseEntity.internalServerError()
                .body(ScrapeResponse.builder()
                    .searchTopic(request.getSearchTopic())
                    .status("error")
                    .message("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    @Operation(
        summary = "Health check",
        description = "Returns the health status of the web scraper service"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Service is healthy",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Health Check Response",
                value = """
                {
                    "status": "UP",
                    "service": "Web Scraper Service",
                    "version": "1.0.0",
                    "timestamp": 1694678400000
                }
                """
            )
        )
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Web Scraper Service",
            "version", "1.0.0",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(
        summary = "Service information",
        description = "Returns detailed information about the web scraper service including available endpoints and supported data types"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Service information retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "Service Info Response",
                value = """
                {
                    "name": "Web Scraper Service",
                    "description": "A Spring Boot service for scraping web data including emails, phone numbers, and descriptions",
                    "version": "1.0.0",
                    "endpoints": {
                        "POST /api/scrape/web": "Scrape web data based on search topic",
                        "GET /api/scrape/health": "Health check endpoint",
                        "GET /api/scrape/info": "Service information"
                    },
                    "supportedDataTypes": ["emails", "phoneNumbers", "socialLinks", "descriptions", "content"]
                }
                """
            )
        )
    )
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Web Scraper Service",
            "description", "A Spring Boot service for scraping web data including emails, phone numbers, and descriptions",
            "version", "1.0.0",
            "endpoints", Map.of(
                "POST /api/scrape/web", "Scrape web data based on search topic",
                "GET /api/scrape/health", "Health check endpoint",
                "GET /api/scrape/info", "Service information"
            ),
            "supportedDataTypes", new String[]{"emails", "phoneNumbers", "socialLinks", "descriptions", "content"}
        ));
    }
}
