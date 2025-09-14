package com.scraper.service;

import com.scraper.model.InstagramScrapeRequest;
import com.scraper.model.InstagramScrapeResponse;
import com.scraper.model.InstagramProfile;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InstagramScrapingService {
    
    private static final Logger log = LoggerFactory.getLogger(InstagramScrapingService.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9]([A-Za-z0-9._%-]*[A-Za-z0-9])?@[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?\\.[A-Za-z]{2,}\\b"
    );
    
    // Phone number regex patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(?:\\(?\\+?1[-.\\)\\s]?)?\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})\\b"
    );
    
    // Website URL pattern
    private static final Pattern WEBSITE_PATTERN = Pattern.compile(
        "https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?"
    );
    
    public InstagramScrapeResponse scrapeInstagramProfiles(InstagramScrapeRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting Instagram scraping for target: {}", request.getTargetHandle());
        
        WebDriver driver = null;
        try {
            // Setup WebDriver
            driver = setupWebDriver(request.getHeadlessMode() != null ? request.getHeadlessMode() : false);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Login to Instagram
            if (!loginToInstagram(driver, wait, request.getUsername(), request.getPassword())) {
                return createErrorResponse(request.getTargetHandle(), "Failed to login to Instagram", startTime);
            }
            
            // Navigate to target profile
            if (!navigateToProfile(driver, wait, request.getTargetHandle())) {
                return createErrorResponse(request.getTargetHandle(), "Failed to navigate to target profile", startTime);
            }
            
            List<InstagramProfile> allProfiles = new ArrayList<>();
            
            // Scrape followers if requested
            if (request.getScrapeFollowers()) {
                List<InstagramProfile> followers = scrapeFollowers(driver, wait, request);
                allProfiles.addAll(followers);
                log.info("Scraped {} followers", followers.size());
            }
            
            // Scrape following if requested
            if (request.getScrapeFollowing()) {
                List<InstagramProfile> following = scrapeFollowing(driver, wait, request);
                allProfiles.addAll(following);
                log.info("Scraped {} following", following.size());
            }
            
            // Calculate statistics
            Map<String, Object> statistics = calculateStatistics(allProfiles);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            return InstagramScrapeResponse.builder()
                .targetHandle(request.getTargetHandle())
                .totalProfiles(allProfiles.size())
                .followersScraped(request.getScrapeFollowers() ? 
                    (int) allProfiles.stream().filter(p -> p.getFollowersCount() != null).count() : 0)
                .followingScraped(request.getScrapeFollowing() ? 
                    (int) allProfiles.stream().filter(p -> p.getFollowingCount() != null).count() : 0)
                .successfulScrapes((int) allProfiles.stream().filter(p -> "success".equals(p.getStatus())).count())
                .failedScrapes((int) allProfiles.stream().filter(p -> "error".equals(p.getStatus())).count())
                .profiles(allProfiles)
                .processingTime(processingTime)
                .status("completed")
                .message("Instagram scraping completed successfully")
                .statistics(statistics)
                .build();
                
        } catch (Exception e) {
            log.error("Error during Instagram scraping: ", e);
            return createErrorResponse(request.getTargetHandle(), "Error: " + e.getMessage(), startTime);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    private WebDriver setupWebDriver(boolean headlessMode) {
        // Use Chromium instead of Chrome for better performance
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Performance optimizations for Chromium
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Disable image loading for faster performance
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-field-trial-config");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--memory-pressure-off");
        options.addArguments("--max_old_space_size=4096");
        options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // Use headless mode for better performance
        if (headlessMode) {
            options.addArguments("--headless");
            log.info("Running browser in headless mode for better performance");
        }
        
        // Additional performance optimizations
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        options.addArguments("--disable-translate");
        options.addArguments("--hide-scrollbars");
        options.addArguments("--mute-audio");
        options.addArguments("--no-first-run");
        options.addArguments("--safebrowsing-disable-auto-update");
        options.addArguments("--disable-client-side-phishing-detection");
        options.addArguments("--disable-component-update");
        options.addArguments("--disable-domain-reliability");
        
        // Set binary path to Chromium if available
        try {
            // Try to use Chromium binary if available
            options.setBinary("/usr/bin/chromium-browser"); // Linux
        } catch (Exception e) {
            try {
                options.setBinary("/Applications/Chromium.app/Contents/MacOS/Chromium"); // macOS
            } catch (Exception e2) {
                // Fall back to default Chrome/Chromium
                log.info("Using default Chrome/Chromium binary");
            }
        }
        
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1280, 720)); // Smaller window for better performance
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Reduced timeout
        
        return driver;
    }
    
    private boolean loginToInstagram(WebDriver driver, WebDriverWait wait, String username, String password) {
        try {
            log.info("Logging into Instagram...");
            driver.get("https://www.instagram.com/accounts/login/");
            
            // Wait for login form
            wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
            
            // Enter username
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.clear();
            usernameField.sendKeys(username);
            
            // Enter password
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.clear();
            passwordField.sendKeys(password);
            
            // Click login button
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
            loginButton.click();
            
            // Wait for login to complete (either success or error)
            Thread.sleep(3000);
            
            // Check if login was successful
            if (driver.getCurrentUrl().contains("/accounts/login/")) {
                log.error("Login failed - still on login page");
                return false;
            }
            
            // Handle "Save Login Info" dialog if it appears
            try {
                WebElement notNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Not Now') or contains(text(), 'Not now')]")));
                notNowButton.click();
                Thread.sleep(1000);
            } catch (TimeoutException e) {
                // Dialog didn't appear, continue
            }
            
            // Handle notifications dialog if it appears
            try {
                WebElement notNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Not Now') or contains(text(), 'Not now')]")));
                notNowButton.click();
                Thread.sleep(1000);
            } catch (TimeoutException e) {
                // Dialog didn't appear, continue
            }
            
            log.info("Successfully logged into Instagram");
            return true;
            
        } catch (Exception e) {
            log.error("Error during Instagram login: ", e);
            return false;
        }
    }
    
    private boolean navigateToProfile(WebDriver driver, WebDriverWait wait, String targetHandle) {
        try {
            log.info("Navigating to profile: {}", targetHandle);
            String profileUrl = "https://www.instagram.com/" + targetHandle + "/";
            driver.get(profileUrl);
            
            // Wait for profile to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
            
            // Check if profile exists
            if (driver.getPageSource().contains("Sorry, this page isn't available")) {
                log.error("Profile not found: {}", targetHandle);
                return false;
            }
            
            log.info("Successfully navigated to profile: {}", targetHandle);
            return true;
            
        } catch (Exception e) {
            log.error("Error navigating to profile: ", e);
            return false;
        }
    }
    
    private List<InstagramProfile> scrapeFollowers(WebDriver driver, WebDriverWait wait, InstagramScrapeRequest request) {
        List<InstagramProfile> followers = new ArrayList<>();
        
        try {
            // Click on followers link
            WebElement followersLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/followers/')]")));
            followersLink.click();
            
            // Wait for followers modal to open
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@role='dialog']//div[contains(@class, 'x1dm5mii')]")));
            
            // Scroll and collect followers
            followers = scrollAndCollectProfiles(driver, wait, request.getMaxFollowers(), request.getDelayMs());
            
            // Close modal
            WebElement closeButton = driver.findElement(By.xpath("//button[@aria-label='Close']"));
            closeButton.click();
            Thread.sleep(1000);
            
        } catch (Exception e) {
            log.error("Error scraping followers: ", e);
        }
        
        return followers;
    }
    
    private List<InstagramProfile> scrapeFollowing(WebDriver driver, WebDriverWait wait, InstagramScrapeRequest request) {
        List<InstagramProfile> following = new ArrayList<>();
        
        try {
            // Click on following link
            WebElement followingLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/following/')]")));
            followingLink.click();
            
            // Wait for following modal to open
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@role='dialog']//div[contains(@class, 'x1dm5mii')]")));
            
            // Scroll and collect following
            following = scrollAndCollectProfiles(driver, wait, request.getMaxFollowing(), request.getDelayMs());
            
            // Close modal
            WebElement closeButton = driver.findElement(By.xpath("//button[@aria-label='Close']"));
            closeButton.click();
            Thread.sleep(1000);
            
        } catch (Exception e) {
            log.error("Error scraping following: ", e);
        }
        
        return following;
    }
    
    private List<InstagramProfile> scrollAndCollectProfiles(WebDriver driver, WebDriverWait wait, int maxProfiles, long delayMs) {
        List<InstagramProfile> profiles = new ArrayList<>();
        Set<String> processedUsernames = new HashSet<>();
        
        try {
            WebElement modal = driver.findElement(By.xpath("//div[@role='dialog']"));
            WebElement scrollableDiv = modal.findElement(By.xpath(".//div[contains(@class, 'x1dm5mii')]"));
            
            int lastSize = 0;
            int stableCount = 0;
            
            while (profiles.size() < maxProfiles && stableCount < 3) {
                // Scroll down
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollTop = arguments[0].scrollTop + arguments[0].offsetHeight;", 
                    scrollableDiv);
                
                Thread.sleep(delayMs);
                
                // Find all profile links in the modal
                List<WebElement> profileLinks = scrollableDiv.findElements(
                    By.xpath(".//a[contains(@href, '/') and not(contains(@href, '/p/')) and not(contains(@href, '/reel/'))]"));
                
                // Process new profiles
                for (WebElement link : profileLinks) {
                    if (profiles.size() >= maxProfiles) break;
                    
                    try {
                        String href = link.getAttribute("href");
                        String username = extractUsernameFromUrl(href);
                        
                        if (username != null && !processedUsernames.contains(username)) {
                            processedUsernames.add(username);
                            
                            // Extract basic info from the link
                            InstagramProfile profile = extractProfileFromLink(link, username);
                            if (profile != null) {
                                profiles.add(profile);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Error processing profile link: ", e);
                    }
                }
                
                // Check if we're still finding new profiles
                if (profiles.size() == lastSize) {
                    stableCount++;
                } else {
                    stableCount = 0;
                    lastSize = profiles.size();
                }
            }
            
        } catch (Exception e) {
            log.error("Error during scrolling and collection: ", e);
        }
        
        return profiles;
    }
    
    private String extractUsernameFromUrl(String url) {
        if (url == null || !url.contains("instagram.com/")) {
            return null;
        }
        
        try {
            String[] parts = url.split("instagram.com/");
            if (parts.length > 1) {
                String username = parts[1].split("/")[0].split("\\?")[0];
                return username.isEmpty() ? null : username;
            }
        } catch (Exception e) {
            log.warn("Error extracting username from URL: {}", url);
        }
        
        return null;
    }
    
    private InstagramProfile extractProfileFromLink(WebElement link, String username) {
        try {
            // Get profile info from the link element
            String fullName = "";
            String bio = "";
            
            try {
                WebElement nameElement = link.findElement(By.xpath(".//span[contains(@class, 'x1lliihq')]"));
                fullName = nameElement.getText();
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // Name not found, use username
                fullName = username;
            }
            
            try {
                WebElement bioElement = link.findElement(By.xpath(".//span[contains(@class, 'x1lliihq') and position()=2]"));
                bio = bioElement.getText();
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // Bio not found
            }
            
            // Extract contact information from bio
            Set<String> emails = extractEmails(bio);
            Set<String> phoneNumbers = extractPhoneNumbers(bio);
            String website = extractWebsite(bio);
            String contact = extractContact(bio);
            String location = extractLocation(bio);
            
            return InstagramProfile.builder()
                .username(username)
                .fullName(fullName)
                .bio(bio)
                .profileUrl("https://instagram.com/" + username)
                .emails(emails)
                .phoneNumbers(phoneNumbers)
                .website(website)
                .contact(contact)
                .location(location)
                .status("success")
                .responseTime(System.currentTimeMillis())
                .build();
                
        } catch (Exception e) {
            log.warn("Error extracting profile from link: ", e);
            return InstagramProfile.builder()
                .username(username)
                .status("error")
                .errorMessage("Failed to extract profile data")
                .responseTime(System.currentTimeMillis())
                .build();
        }
    }
    
    private Set<String> extractEmails(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<String> emails = new HashSet<>();
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        while (matcher.find()) {
            emails.add(matcher.group());
        }
        return emails;
    }
    
    private Set<String> extractPhoneNumbers(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<String> phones = new HashSet<>();
        Matcher matcher = PHONE_PATTERN.matcher(text);
        while (matcher.find()) {
            phones.add(matcher.group());
        }
        return phones;
    }
    
    private String extractWebsite(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        Matcher matcher = WEBSITE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
    
    private String extractContact(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Look for common contact patterns
        String[] contactPatterns = {
            "contact:", "email:", "reach me:", "dm me:", "message me:",
            "get in touch:", "business:", "collab:", "collaboration:"
        };
        
        String lowerText = text.toLowerCase();
        for (String pattern : contactPatterns) {
            int index = lowerText.indexOf(pattern);
            if (index != -1) {
                String contact = text.substring(index + pattern.length()).trim();
                // Take first line or until next pattern
                String[] lines = contact.split("\n");
                if (lines.length > 0) {
                    return lines[0].trim();
                }
            }
        }
        
        return null;
    }
    
    private String extractLocation(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Simple location extraction - look for common patterns
        String[] locationPatterns = {
            "📍", "🌍", "📍", "based in", "located in", "from"
        };
        
        String lowerText = text.toLowerCase();
        for (String pattern : locationPatterns) {
            int index = lowerText.indexOf(pattern);
            if (index != -1) {
                String location = text.substring(index + pattern.length()).trim();
                String[] lines = location.split("\n");
                if (lines.length > 0) {
                    return lines[0].trim();
                }
            }
        }
        
        return null;
    }
    
    private Map<String, Object> calculateStatistics(List<InstagramProfile> profiles) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalEmails = profiles.stream()
            .mapToLong(p -> p.getEmails() != null ? p.getEmails().size() : 0)
            .sum();
        
        long totalPhones = profiles.stream()
            .mapToLong(p -> p.getPhoneNumbers() != null ? p.getPhoneNumbers().size() : 0)
            .sum();
        
        long profilesWithContact = profiles.stream()
            .filter(p -> (p.getEmails() != null && !p.getEmails().isEmpty()) || 
                        (p.getPhoneNumbers() != null && !p.getPhoneNumbers().isEmpty()) ||
                        (p.getContact() != null && !p.getContact().isEmpty()))
            .count();
        
        stats.put("totalEmails", totalEmails);
        stats.put("totalPhoneNumbers", totalPhones);
        stats.put("profilesWithContact", profilesWithContact);
        stats.put("contactRate", profiles.size() > 0 ? (double) profilesWithContact / profiles.size() * 100 : 0);
        
        return stats;
    }
    
    private InstagramScrapeResponse createErrorResponse(String targetHandle, String errorMessage, long startTime) {
        return InstagramScrapeResponse.builder()
            .targetHandle(targetHandle)
            .totalProfiles(0)
            .followersScraped(0)
            .followingScraped(0)
            .successfulScrapes(0)
            .failedScrapes(0)
            .profiles(new ArrayList<>())
            .processingTime(System.currentTimeMillis() - startTime)
            .status("error")
            .message(errorMessage)
            .build();
    }
}
