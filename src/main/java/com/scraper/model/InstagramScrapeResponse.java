package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "Response model for Instagram scraping")
public class InstagramScrapeResponse {
    
    @Schema(description = "Target Instagram handle that was scraped", example = "fashion_influencer")
    private String targetHandle;
    
    @Schema(description = "Total number of profiles scraped", example = "1500")
    private Integer totalProfiles;
    
    @Schema(description = "Number of followers scraped", example = "1000")
    private Integer followersScraped;
    
    @Schema(description = "Number of following scraped", example = "500")
    private Integer followingScraped;
    
    @Schema(description = "Number of successful scrapes", example = "1450")
    private Integer successfulScrapes;
    
    @Schema(description = "Number of failed scrapes", example = "50")
    private Integer failedScrapes;
    
    @Schema(description = "List of scraped Instagram profiles")
    private List<InstagramProfile> profiles;
    
    @Schema(description = "Processing time in milliseconds", example = "45000")
    private Long processingTime;
    
    @Schema(description = "Overall status", example = "completed")
    private String status;
    
    @Schema(description = "Status message", example = "Instagram scraping completed successfully")
    private String message;
    
    @Schema(description = "Session information")
    private Map<String, Object> sessionInfo;
    
    @Schema(description = "Statistics about scraped data")
    private Map<String, Object> statistics;

    // Constructors
    public InstagramScrapeResponse() {}

    public InstagramScrapeResponse(String targetHandle, Integer totalProfiles, Integer followersScraped, 
                                  Integer followingScraped, Integer successfulScrapes, Integer failedScrapes, 
                                  List<InstagramProfile> profiles, Long processingTime, String status, 
                                  String message, Map<String, Object> sessionInfo, Map<String, Object> statistics) {
        this.targetHandle = targetHandle;
        this.totalProfiles = totalProfiles;
        this.followersScraped = followersScraped;
        this.followingScraped = followingScraped;
        this.successfulScrapes = successfulScrapes;
        this.failedScrapes = failedScrapes;
        this.profiles = profiles;
        this.processingTime = processingTime;
        this.status = status;
        this.message = message;
        this.sessionInfo = sessionInfo;
        this.statistics = statistics;
    }

    // Getters and Setters
    public String getTargetHandle() { return targetHandle; }
    public void setTargetHandle(String targetHandle) { this.targetHandle = targetHandle; }
    
    public Integer getTotalProfiles() { return totalProfiles; }
    public void setTotalProfiles(Integer totalProfiles) { this.totalProfiles = totalProfiles; }
    
    public Integer getFollowersScraped() { return followersScraped; }
    public void setFollowersScraped(Integer followersScraped) { this.followersScraped = followersScraped; }
    
    public Integer getFollowingScraped() { return followingScraped; }
    public void setFollowingScraped(Integer followingScraped) { this.followingScraped = followingScraped; }
    
    public Integer getSuccessfulScrapes() { return successfulScrapes; }
    public void setSuccessfulScrapes(Integer successfulScrapes) { this.successfulScrapes = successfulScrapes; }
    
    public Integer getFailedScrapes() { return failedScrapes; }
    public void setFailedScrapes(Integer failedScrapes) { this.failedScrapes = failedScrapes; }
    
    public List<InstagramProfile> getProfiles() { return profiles; }
    public void setProfiles(List<InstagramProfile> profiles) { this.profiles = profiles; }
    
    public Long getProcessingTime() { return processingTime; }
    public void setProcessingTime(Long processingTime) { this.processingTime = processingTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getSessionInfo() { return sessionInfo; }
    public void setSessionInfo(Map<String, Object> sessionInfo) { this.sessionInfo = sessionInfo; }
    
    public Map<String, Object> getStatistics() { return statistics; }
    public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String targetHandle;
        private Integer totalProfiles;
        private Integer followersScraped;
        private Integer followingScraped;
        private Integer successfulScrapes;
        private Integer failedScrapes;
        private List<InstagramProfile> profiles;
        private Long processingTime;
        private String status;
        private String message;
        private Map<String, Object> sessionInfo;
        private Map<String, Object> statistics;

        public Builder targetHandle(String targetHandle) { this.targetHandle = targetHandle; return this; }
        public Builder totalProfiles(Integer totalProfiles) { this.totalProfiles = totalProfiles; return this; }
        public Builder followersScraped(Integer followersScraped) { this.followersScraped = followersScraped; return this; }
        public Builder followingScraped(Integer followingScraped) { this.followingScraped = followingScraped; return this; }
        public Builder successfulScrapes(Integer successfulScrapes) { this.successfulScrapes = successfulScrapes; return this; }
        public Builder failedScrapes(Integer failedScrapes) { this.failedScrapes = failedScrapes; return this; }
        public Builder profiles(List<InstagramProfile> profiles) { this.profiles = profiles; return this; }
        public Builder processingTime(Long processingTime) { this.processingTime = processingTime; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder sessionInfo(Map<String, Object> sessionInfo) { this.sessionInfo = sessionInfo; return this; }
        public Builder statistics(Map<String, Object> statistics) { this.statistics = statistics; return this; }

        public InstagramScrapeResponse build() {
            return new InstagramScrapeResponse(targetHandle, totalProfiles, followersScraped, followingScraped, 
                                             successfulScrapes, failedScrapes, profiles, processingTime, 
                                             status, message, sessionInfo, statistics);
        }
    }
}