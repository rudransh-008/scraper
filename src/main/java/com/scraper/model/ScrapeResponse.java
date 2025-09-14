package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "Response object containing the results of web scraping operations")
public class ScrapeResponse {
    
    @Schema(description = "The search topic that was scraped", example = "software development companies")
    private String searchTopic;
    
    @Schema(description = "Total number of URLs that were attempted to be scraped", example = "5")
    private int totalResults;
    
    @Schema(description = "Number of URLs that were successfully scraped", example = "4")
    private int successfulScrapes;
    
    @Schema(description = "Number of URLs that failed to be scraped", example = "1")
    private int failedScrapes;
    
    @Schema(description = "List of scraped data from each URL")
    private List<ScrapedData> results;
    
    @Schema(description = "Metadata about the scraping operation", example = "{\"searchEngine\": \"google\", \"language\": \"en\", \"country\": \"us\"}")
    private Map<String, Object> metadata;
    
    @Schema(description = "Total time taken for the entire scraping operation in milliseconds", example = "5000")
    private long processingTime;
    
    @Schema(description = "Overall status of the scraping operation", example = "completed", allowableValues = {"completed", "error"})
    private String status;
    
    @Schema(description = "Status message describing the result", example = "Scraping completed successfully")
    private String message;
    
    // Constructors
    public ScrapeResponse() {}
    
    public ScrapeResponse(String searchTopic, int totalResults, int successfulScrapes, 
                         int failedScrapes, List<ScrapedData> results, Map<String, Object> metadata, 
                         long processingTime, String status, String message) {
        this.searchTopic = searchTopic;
        this.totalResults = totalResults;
        this.successfulScrapes = successfulScrapes;
        this.failedScrapes = failedScrapes;
        this.results = results;
        this.metadata = metadata;
        this.processingTime = processingTime;
        this.status = status;
        this.message = message;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String searchTopic;
        private int totalResults;
        private int successfulScrapes;
        private int failedScrapes;
        private List<ScrapedData> results;
        private Map<String, Object> metadata;
        private long processingTime;
        private String status;
        private String message;
        
        public Builder searchTopic(String searchTopic) { this.searchTopic = searchTopic; return this; }
        public Builder totalResults(int totalResults) { this.totalResults = totalResults; return this; }
        public Builder successfulScrapes(int successfulScrapes) { this.successfulScrapes = successfulScrapes; return this; }
        public Builder failedScrapes(int failedScrapes) { this.failedScrapes = failedScrapes; return this; }
        public Builder results(List<ScrapedData> results) { this.results = results; return this; }
        public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
        public Builder processingTime(long processingTime) { this.processingTime = processingTime; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        
        public ScrapeResponse build() {
            return new ScrapeResponse(searchTopic, totalResults, successfulScrapes, failedScrapes, 
                                    results, metadata, processingTime, status, message);
        }
    }
    
    // Getters and Setters
    public String getSearchTopic() { return searchTopic; }
    public void setSearchTopic(String searchTopic) { this.searchTopic = searchTopic; }
    
    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }
    
    public int getSuccessfulScrapes() { return successfulScrapes; }
    public void setSuccessfulScrapes(int successfulScrapes) { this.successfulScrapes = successfulScrapes; }
    
    public int getFailedScrapes() { return failedScrapes; }
    public void setFailedScrapes(int failedScrapes) { this.failedScrapes = failedScrapes; }
    
    public List<ScrapedData> getResults() { return results; }
    public void setResults(List<ScrapedData> results) { this.results = results; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
