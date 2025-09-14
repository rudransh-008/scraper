package com.scraper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Instagram profile data")
public class InstagramProfile {
    
    @Schema(description = "Instagram username", example = "fashion_influencer")
    private String username;
    
    @Schema(description = "Full name", example = "Fashion Influencer")
    private String fullName;
    
    @Schema(description = "Bio text", example = "Fashion blogger | Contact: fashion@email.com")
    private String bio;
    
    @Schema(description = "Profile picture URL", example = "https://instagram.com/p/profile_pic.jpg")
    private String profilePictureUrl;
    
    @Schema(description = "Number of followers", example = "50000")
    private Long followersCount;
    
    @Schema(description = "Number of following", example = "1000")
    private Long followingCount;
    
    @Schema(description = "Number of posts", example = "500")
    private Long postsCount;
    
    @Schema(description = "Is verified account", example = "false")
    private Boolean isVerified;
    
    @Schema(description = "Is private account", example = "false")
    private Boolean isPrivate;
    
    @Schema(description = "Is business account", example = "true")
    private Boolean isBusiness;
    
    @Schema(description = "Website URL from bio", example = "https://fashionblog.com")
    private String website;
    
    @Schema(description = "Contact information extracted from bio", example = "fashion@email.com")
    private String contact;
    
    @Schema(description = "Email addresses found in bio", example = "[\"fashion@email.com\", \"contact@fashion.com\"]")
    private Set<String> emails;
    
    @Schema(description = "Phone numbers found in bio", example = "[\"+1234567890\", \"(555) 123-4567\"]")
    private Set<String> phoneNumbers;
    
    @Schema(description = "Location from bio", example = "New York, NY")
    private String location;
    
    @Schema(description = "External links found in bio", example = "[\"https://linktr.ee/fashion\", \"https://fashionblog.com\"]")
    private Set<String> externalLinks;
    
    @Schema(description = "Profile URL", example = "https://instagram.com/fashion_influencer")
    private String profileUrl;
    
    @Schema(description = "Scraping status", example = "success")
    private String status;
    
    @Schema(description = "Error message if scraping failed", example = "Profile is private")
    private String errorMessage;
    
    @Schema(description = "Response time in milliseconds", example = "1500")
    private Long responseTime;

    // Constructors
    public InstagramProfile() {}

    public InstagramProfile(String username, String fullName, String bio, String profilePictureUrl, 
                           Long followersCount, Long followingCount, Long postsCount, Boolean isVerified, 
                           Boolean isPrivate, Boolean isBusiness, String website, String contact, 
                           Set<String> emails, Set<String> phoneNumbers, String location, 
                           Set<String> externalLinks, String profileUrl, String status, 
                           String errorMessage, Long responseTime) {
        this.username = username;
        this.fullName = fullName;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.postsCount = postsCount;
        this.isVerified = isVerified;
        this.isPrivate = isPrivate;
        this.isBusiness = isBusiness;
        this.website = website;
        this.contact = contact;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
        this.location = location;
        this.externalLinks = externalLinks;
        this.profileUrl = profileUrl;
        this.status = status;
        this.errorMessage = errorMessage;
        this.responseTime = responseTime;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    public Long getFollowersCount() { return followersCount; }
    public void setFollowersCount(Long followersCount) { this.followersCount = followersCount; }
    
    public Long getFollowingCount() { return followingCount; }
    public void setFollowingCount(Long followingCount) { this.followingCount = followingCount; }
    
    public Long getPostsCount() { return postsCount; }
    public void setPostsCount(Long postsCount) { this.postsCount = postsCount; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }
    
    public Boolean getIsBusiness() { return isBusiness; }
    public void setIsBusiness(Boolean isBusiness) { this.isBusiness = isBusiness; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public Set<String> getEmails() { return emails; }
    public void setEmails(Set<String> emails) { this.emails = emails; }
    
    public Set<String> getPhoneNumbers() { return phoneNumbers; }
    public void setPhoneNumbers(Set<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Set<String> getExternalLinks() { return externalLinks; }
    public void setExternalLinks(Set<String> externalLinks) { this.externalLinks = externalLinks; }
    
    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String fullName;
        private String bio;
        private String profilePictureUrl;
        private Long followersCount;
        private Long followingCount;
        private Long postsCount;
        private Boolean isVerified;
        private Boolean isPrivate;
        private Boolean isBusiness;
        private String website;
        private String contact;
        private Set<String> emails;
        private Set<String> phoneNumbers;
        private String location;
        private Set<String> externalLinks;
        private String profileUrl;
        private String status;
        private String errorMessage;
        private Long responseTime;

        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder followersCount(Long followersCount) { this.followersCount = followersCount; return this; }
        public Builder followingCount(Long followingCount) { this.followingCount = followingCount; return this; }
        public Builder postsCount(Long postsCount) { this.postsCount = postsCount; return this; }
        public Builder isVerified(Boolean isVerified) { this.isVerified = isVerified; return this; }
        public Builder isPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; return this; }
        public Builder isBusiness(Boolean isBusiness) { this.isBusiness = isBusiness; return this; }
        public Builder website(String website) { this.website = website; return this; }
        public Builder contact(String contact) { this.contact = contact; return this; }
        public Builder emails(Set<String> emails) { this.emails = emails; return this; }
        public Builder phoneNumbers(Set<String> phoneNumbers) { this.phoneNumbers = phoneNumbers; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder externalLinks(Set<String> externalLinks) { this.externalLinks = externalLinks; return this; }
        public Builder profileUrl(String profileUrl) { this.profileUrl = profileUrl; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder responseTime(Long responseTime) { this.responseTime = responseTime; return this; }

        public InstagramProfile build() {
            return new InstagramProfile(username, fullName, bio, profilePictureUrl, followersCount, 
                                      followingCount, postsCount, isVerified, isPrivate, isBusiness, 
                                      website, contact, emails, phoneNumbers, location, externalLinks, 
                                      profileUrl, status, errorMessage, responseTime);
        }
    }
}