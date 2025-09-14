package com.scraper.controller;

import com.scraper.model.ScrapeRequest;
import com.scraper.model.ScrapeResponse;
import com.scraper.model.InstagramScrapeRequest;
import com.scraper.model.InstagramScrapeResponse;
import com.scraper.service.WebScrapingService;
import com.scraper.service.InstagramScrapingService;
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

import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
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

    @Autowired
    private InstagramScrapingService instagramScrapingService;

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
                "POST /api/scrape/instagram", "Scrape Instagram followers/following with login credentials",
                "GET /api/scrape/health", "Health check endpoint",
                "GET /api/scrape/info", "Service information"
            ),
            "supportedDataTypes", new String[]{"emails", "phoneNumbers", "socialLinks", "descriptions", "content"}
        ));
    }
    
    @Operation(
        summary = "Scrape Instagram profiles",
        description = "Scrapes Instagram followers and following from a target profile. Requires Instagram login credentials. Extracts bio data, contact information, and profile details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Instagram scraping completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InstagramScrapeResponse.class),
                examples = @ExampleObject(
                    name = "Successful Instagram scraping",
                    value = """
                    {
                        "targetHandle": "fashion_influencer",
                        "totalProfiles": 1500,
                        "followersScraped": 1000,
                        "followingScraped": 500,
                        "successfulScrapes": 1450,
                        "failedScrapes": 50,
                        "profiles": [
                            {
                                "username": "fashion_blogger",
                                "fullName": "Fashion Blogger",
                                "bio": "Fashion blogger | Contact: fashion@email.com",
                                "emails": ["fashion@email.com"],
                                "phoneNumbers": [],
                                "website": "https://fashionblog.com",
                                "status": "success"
                            }
                        ],
                        "processingTime": 45000,
                        "status": "completed",
                        "message": "Instagram scraping completed successfully"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - invalid parameters",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/instagram", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> scrapeInstagramProfiles(
        @Parameter(
            description = "Instagram scraping request containing login credentials and target profile",
            required = true,
            schema = @Schema(implementation = InstagramScrapeRequest.class)
        )
        @RequestBody InstagramScrapeRequest request
    ) {
        log.info("Received Instagram scraping request for target: {}", request.getTargetHandle());
        
        try {
            InstagramScrapeResponse response = instagramScrapingService.scrapeInstagramProfiles(request);
            
            // If CSV export is requested, return CSV file
            if (request.getExportAsCsv() != null && request.getExportAsCsv()) {
                String csvContent = convertInstagramToCsv(response.getProfiles(), request.getFieldsToExtract());
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", 
                    "instagram_data_" + request.getTargetHandle() + "_" + System.currentTimeMillis() + ".csv");
                
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
            log.error("Error processing Instagram scraping request: ", e);
            return ResponseEntity.internalServerError()
                .body(InstagramScrapeResponse.builder()
                    .targetHandle(request.getTargetHandle())
                    .status("error")
                    .message("Error processing request: " + e.getMessage())
                    .build());
        }
    }
    
    private String convertInstagramToCsv(List<com.scraper.model.InstagramProfile> profiles, Set<String> fieldsToExtract) {
        try (StringWriter writer = new StringWriter();
             com.opencsv.CSVWriter csvWriter = new com.opencsv.CSVWriter(writer)) {
            
            List<String> headers = new ArrayList<>();
            headers.add("Username");
            headers.add("Full Name");
            headers.add("Profile URL");
            headers.add("Status");
            
            if (fieldsToExtract == null || fieldsToExtract.isEmpty()) {
                fieldsToExtract = Set.of("bio", "contact", "email", "phone", "website", "location");
            }
            
            if (fieldsToExtract.contains("bio")) headers.add("Bio");
            if (fieldsToExtract.contains("contact")) headers.add("Contact");
            if (fieldsToExtract.contains("email")) headers.add("Emails");
            if (fieldsToExtract.contains("phone")) headers.add("Phone Numbers");
            if (fieldsToExtract.contains("website")) headers.add("Website");
            if (fieldsToExtract.contains("location")) headers.add("Location");
            if (fieldsToExtract.contains("errorMessage")) headers.add("Error Message");
            
            csvWriter.writeNext(headers.toArray(new String[0]));
            
            for (com.scraper.model.InstagramProfile profile : profiles) {
                List<String> row = new ArrayList<>();
                row.add(profile.getUsername() != null ? profile.getUsername() : "");
                row.add(profile.getFullName() != null ? profile.getFullName() : "");
                row.add(profile.getProfileUrl() != null ? profile.getProfileUrl() : "");
                row.add(profile.getStatus() != null ? profile.getStatus() : "");
                
                if (fieldsToExtract.contains("bio")) {
                    row.add(profile.getBio() != null ? profile.getBio().replaceAll("\\s+", " ").trim() : "");
                }
                if (fieldsToExtract.contains("contact")) {
                    row.add(profile.getContact() != null ? profile.getContact() : "");
                }
                if (fieldsToExtract.contains("email")) {
                    row.add(profile.getEmails() != null ? String.join("; ", profile.getEmails()) : "");
                }
                if (fieldsToExtract.contains("phone")) {
                    row.add(profile.getPhoneNumbers() != null ? String.join("; ", profile.getPhoneNumbers()) : "");
                }
                if (fieldsToExtract.contains("website")) {
                    row.add(profile.getWebsite() != null ? profile.getWebsite() : "");
                }
                if (fieldsToExtract.contains("location")) {
                    row.add(profile.getLocation() != null ? profile.getLocation() : "");
                }
                if (fieldsToExtract.contains("errorMessage")) {
                    row.add(profile.getErrorMessage() != null ? profile.getErrorMessage() : "");
                }
                
                csvWriter.writeNext(row.toArray(new String[0]));
            }
            
            return writer.toString();
        } catch (Exception e) {
            log.error("Error converting Instagram data to CSV: ", e);
            throw new RuntimeException("Failed to convert Instagram data to CSV", e);
        }
    }
}
