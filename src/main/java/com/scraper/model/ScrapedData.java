package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Data extracted from a single URL during scraping")
public class ScrapedData {
    
    @Schema(description = "The URL that was scraped", example = "https://example.com")
    private String url;
    
    @Schema(description = "Page title extracted from the HTML", example = "Example Company - Software Development")
    private String title;
    
    @Schema(description = "Page description or meta description", example = "Leading software development company specializing in web applications")
    private String description;
    
    @Schema(description = "Set of email addresses found on the page", example = "[\"contact@example.com\", \"info@example.com\"]")
    private Set<String> emails;
    
    @Schema(description = "Set of phone numbers found on the page", example = "[\"+1-555-123-4567\", \"(555) 987-6543\"]")
    private Set<String> phoneNumbers;
    
    @Schema(description = "Set of social media links found on the page", example = "[\"https://linkedin.com/company/example\", \"https://twitter.com/example\"]")
    private Set<String> socialLinks;
    
    @Schema(description = "Main content text extracted from the page", example = "We are a leading software development company...")
    private String content;
    
    @Schema(description = "Domain name of the scraped URL", example = "example.com")
    private String domain;
    
    @Schema(description = "Status of the scraping operation", example = "success", allowableValues = {"success", "error"})
    private String status;
    
    @Schema(description = "Time taken to scrape the URL in milliseconds", example = "1500")
    private long responseTime;
    
    @Schema(description = "Error message if scraping failed", example = "Connection timeout")
    private String errorMessage;
    
    // Constructors
    public ScrapedData() {}
    
    public ScrapedData(String url, String title, String description, Set<String> emails, 
                      Set<String> phoneNumbers, Set<String> socialLinks, String content, 
                      String domain, String status, long responseTime, String errorMessage) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
        this.socialLinks = socialLinks;
        this.content = content;
        this.domain = domain;
        this.status = status;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String url;
        private String title;
        private String description;
        private Set<String> emails;
        private Set<String> phoneNumbers;
        private Set<String> socialLinks;
        private String content;
        private String domain;
        private String status;
        private long responseTime;
        private String errorMessage;
        
        public Builder url(String url) { this.url = url; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder emails(Set<String> emails) { this.emails = emails; return this; }
        public Builder phoneNumbers(Set<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; return this; }
        public Builder socialLinks(Set<String> socialLinks) { this.socialLinks = socialLinks; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder domain(String domain) { this.domain = domain; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder responseTime(long responseTime) { this.responseTime = responseTime; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        
        public ScrapedData build() {
            return new ScrapedData(url, title, description, emails, phoneNumbers, socialLinks, 
                                 content, domain, status, responseTime, errorMessage);
        }
    }
    
    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Set<String> getEmails() { return emails; }
    public void setEmails(Set<String> emails) { this.emails = emails; }
    
    public Set<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(Set<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }
    
    public Set<String> getSocialLinks() { return socialLinks; }
    public void setSocialLinks(Set<String> socialLinks) { this.socialLinks = socialLinks; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
