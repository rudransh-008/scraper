package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for web scraping operations")
public class ScrapeRequest {
    
    @Schema(description = "The search topic or keyword to scrape data for", example = "software development companies", required = true)
    @NotBlank(message = "Search topic is required")
    private String searchTopic;
    
    @Schema(description = "Maximum number of URLs to scrape", example = "5", minimum = "1", maximum = "50", required = true)
    @NotNull(message = "Max results is required")
    private Integer maxResults = 10;
    
    @Schema(description = "Search engine to use for finding URLs", example = "google", allowableValues = {"google", "bing", "duckduckgo"})
    private String searchEngine = "google";
    
    @Schema(description = "Language for search results", example = "en", allowableValues = {"en", "es", "fr", "de", "it"})
    private String language = "en";
    
    @Schema(description = "Country code for localized search results", example = "us", allowableValues = {"us", "uk", "ca", "au", "de"})
    private String country = "us";
    
    @Schema(description = "Fields to extract from scraped data", example = "[\"emails\", \"phoneNumbers\", \"socialLinks\"]", allowableValues = {"emails", "phoneNumbers", "socialLinks", "title", "description", "content", "domain"})
    private java.util.Set<String> fieldsToExtract = java.util.Set.of("emails", "phoneNumbers", "socialLinks", "title", "description", "content", "domain");
    
    @Schema(description = "Whether to export results as CSV file", example = "false")
    private Boolean exportAsCsv = false;
    
    // Constructors
    public ScrapeRequest() {}
    
    public ScrapeRequest(String searchTopic, Integer maxResults, String searchEngine, String language, String country) {
        this.searchTopic = searchTopic;
        this.maxResults = maxResults;
        this.searchEngine = searchEngine;
        this.language = language;
        this.country = country;
    }
    
    public ScrapeRequest(String searchTopic, Integer maxResults, String searchEngine, String language, String country, java.util.Set<String> fieldsToExtract, Boolean exportAsCsv) {
        this.searchTopic = searchTopic;
        this.maxResults = maxResults;
        this.searchEngine = searchEngine;
        this.language = language;
        this.country = country;
        this.fieldsToExtract = fieldsToExtract;
        this.exportAsCsv = exportAsCsv;
    }
    
    // Getters and Setters
    public String getSearchTopic() { return searchTopic; }
    public void setSearchTopic(String searchTopic) { this.searchTopic = searchTopic; }
    
    public Integer getMaxResults() { return maxResults; }
    public void setMaxResults(Integer maxResults) { this.maxResults = maxResults; }
    
    public String getSearchEngine() { return searchEngine; }
    public void setSearchEngine(String searchEngine) { this.searchEngine = searchEngine; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public java.util.Set<String> getFieldsToExtract() { return fieldsToExtract; }
    public void setFieldsToExtract(java.util.Set<String> fieldsToExtract) { this.fieldsToExtract = fieldsToExtract; }
    
    public Boolean getExportAsCsv() { return exportAsCsv; }
    public void setExportAsCsv(Boolean exportAsCsv) { this.exportAsCsv = exportAsCsv; }
}
