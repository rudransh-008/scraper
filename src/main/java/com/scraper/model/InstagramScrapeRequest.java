package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Set;

@Schema(description = "Request model for Instagram scraping")
public class InstagramScrapeRequest {
    
    @NotBlank(message = "Instagram username is required")
    @Schema(description = "Instagram username to scrape", example = "fashion_influencer", required = true)
    private String username;
    
    @NotBlank(message = "Instagram password is required")
    @Schema(description = "Instagram password for authentication", example = "your_password", required = true)
    private String password;
    
    @NotBlank(message = "Target Instagram handle is required")
    @Schema(description = "Target Instagram handle to scrape followers/following from", example = "target_user", required = true)
    private String targetHandle;
    
    @Min(value = 1, message = "Max followers must be at least 1")
    @Max(value = 10000, message = "Max followers cannot exceed 10000")
    @Schema(description = "Maximum number of followers to scrape", example = "1000", minimum = "1", maximum = "10000")
    private Integer maxFollowers = 1000;
    
    @Min(value = 1, message = "Max following must be at least 1")
    @Max(value = 10000, message = "Max following cannot exceed 10000")
    @Schema(description = "Maximum number of following to scrape", example = "500", minimum = "1", maximum = "10000")
    private Integer maxFollowing = 500;
    
    @Schema(description = "Whether to scrape followers", example = "true")
    private Boolean scrapeFollowers = true;
    
    @Schema(description = "Whether to scrape following", example = "true")
    private Boolean scrapeFollowing = true;
    
    @Schema(description = "Fields to extract from Instagram profiles", example = "[\"bio\", \"contact\", \"email\", \"phone\"]", allowableValues = {"bio", "contact", "email", "phone", "website", "location"})
    private Set<String> fieldsToExtract = Set.of("bio", "contact", "email", "phone", "website", "location");
    
    @Schema(description = "Whether to export results as CSV file", example = "false")
    private Boolean exportAsCsv = false;
    
    @Schema(description = "Delay between requests in milliseconds", example = "2000", minimum = "1000", maximum = "10000")
    private Long delayMs = 2000L;
    
    @Schema(description = "Whether to save login session for reuse", example = "true")
    private Boolean saveSession = true;
    
    @Schema(description = "Whether to run browser in headless mode for better performance", example = "false")
    private Boolean headlessMode = false;

    // Constructors
    public InstagramScrapeRequest() {}

    public InstagramScrapeRequest(String username, String password, String targetHandle, Integer maxFollowers, 
                                 Integer maxFollowing, Boolean scrapeFollowers, Boolean scrapeFollowing, 
                                 Set<String> fieldsToExtract, Boolean exportAsCsv, Long delayMs, Boolean saveSession, Boolean headlessMode) {
        this.username = username;
        this.password = password;
        this.targetHandle = targetHandle;
        this.maxFollowers = maxFollowers;
        this.maxFollowing = maxFollowing;
        this.scrapeFollowers = scrapeFollowers;
        this.scrapeFollowing = scrapeFollowing;
        this.fieldsToExtract = fieldsToExtract;
        this.exportAsCsv = exportAsCsv;
        this.delayMs = delayMs;
        this.saveSession = saveSession;
        this.headlessMode = headlessMode;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTargetHandle() { return targetHandle; }
    public void setTargetHandle(String targetHandle) { this.targetHandle = targetHandle; }
    
    public Integer getMaxFollowers() { return maxFollowers; }
    public void setMaxFollowers(Integer maxFollowers) { this.maxFollowers = maxFollowers; }
    
    public Integer getMaxFollowing() { return maxFollowing; }
    public void setMaxFollowing(Integer maxFollowing) { this.maxFollowing = maxFollowing; }
    
    public Boolean getScrapeFollowers() { return scrapeFollowers; }
    public void setScrapeFollowers(Boolean scrapeFollowers) { this.scrapeFollowers = scrapeFollowers; }
    
    public Boolean getScrapeFollowing() { return scrapeFollowing; }
    public void setScrapeFollowing(Boolean scrapeFollowing) { this.scrapeFollowing = scrapeFollowing; }
    
    public Set<String> getFieldsToExtract() { return fieldsToExtract; }
    public void setFieldsToExtract(Set<String> fieldsToExtract) { this.fieldsToExtract = fieldsToExtract; }
    
    public Boolean getExportAsCsv() { return exportAsCsv; }
    public void setExportAsCsv(Boolean exportAsCsv) { this.exportAsCsv = exportAsCsv; }
    
    public Long getDelayMs() { return delayMs; }
    public void setDelayMs(Long delayMs) { this.delayMs = delayMs; }
    
    public Boolean getSaveSession() { return saveSession; }
    public void setSaveSession(Boolean saveSession) { this.saveSession = saveSession; }
    
    public Boolean getHeadlessMode() { return headlessMode; }
    public void setHeadlessMode(Boolean headlessMode) { this.headlessMode = headlessMode; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private String targetHandle;
        private Integer maxFollowers = 1000;
        private Integer maxFollowing = 500;
        private Boolean scrapeFollowers = true;
        private Boolean scrapeFollowing = true;
        private Set<String> fieldsToExtract = Set.of("bio", "contact", "email", "phone", "website", "location");
        private Boolean exportAsCsv = false;
        private Long delayMs = 2000L;
        private Boolean saveSession = true;
        private Boolean headlessMode = false;

        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder targetHandle(String targetHandle) { this.targetHandle = targetHandle; return this; }
        public Builder maxFollowers(Integer maxFollowers) { this.maxFollowers = maxFollowers; return this; }
        public Builder maxFollowing(Integer maxFollowing) { this.maxFollowing = maxFollowing; return this; }
        public Builder scrapeFollowers(Boolean scrapeFollowers) { this.scrapeFollowers = scrapeFollowers; return this; }
        public Builder scrapeFollowing(Boolean scrapeFollowing) { this.scrapeFollowing = scrapeFollowing; return this; }
        public Builder fieldsToExtract(Set<String> fieldsToExtract) { this.fieldsToExtract = fieldsToExtract; return this; }
        public Builder exportAsCsv(Boolean exportAsCsv) { this.exportAsCsv = exportAsCsv; return this; }
        public Builder delayMs(Long delayMs) { this.delayMs = delayMs; return this; }
        public Builder saveSession(Boolean saveSession) { this.saveSession = saveSession; return this; }
        public Builder headlessMode(Boolean headlessMode) { this.headlessMode = headlessMode; return this; }

        public InstagramScrapeRequest build() {
            return new InstagramScrapeRequest(username, password, targetHandle, maxFollowers, maxFollowing, 
                                            scrapeFollowers, scrapeFollowing, fieldsToExtract, exportAsCsv, delayMs, saveSession, headlessMode);
        }
    }
}