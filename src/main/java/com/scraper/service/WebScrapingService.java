package com.scraper.service;

import com.scraper.model.ScrapeRequest;
import com.scraper.model.ScrapedData;
import com.scraper.model.ScrapeResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.opencsv.CSVWriter;

@Service
public class WebScrapingService {
    
    private static final Logger log = LoggerFactory.getLogger(WebScrapingService.class);

    @Value("${scraper.user-agent}")
    private String userAgent;

    @Value("${scraper.timeout}")
    private int timeout;

    @Value("${scraper.max-retries}")
    private int maxRetries;

    @Value("${scraper.rate-limit-delay}")
    private long rateLimitDelay;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9]([A-Za-z0-9._%-]*[A-Za-z0-9])?@[A-Za-z0-9]([A-Za-z0-9.-]*[A-Za-z0-9])?\\.[A-Za-z]{2,}\\b"
    );

    // Phone number regex patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(?:\\(?\\+?1[-.\\)\\s]?)?\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})\\b"
    );

    // Social media patterns
    private static final Pattern SOCIAL_PATTERN = Pattern.compile(
        "(?:https?://)?(?:www\\.)?(?:instagram\\.com|twitter\\.com|facebook\\.com|linkedin\\.com|youtube\\.com|github\\.com|medium\\.com|reddit\\.com|pinterest\\.com|tiktok\\.com|snapchat\\.com)/[\\w\\-./]+"
    );

    public ScrapeResponse scrapeWebData(ScrapeRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting web scraping for topic: {}", request.getSearchTopic());

        try {
            // Get search results URLs
            List<String> urls = getSearchResults(request);
            log.info("Found {} URLs for topic: {}", urls.size(), request.getSearchTopic());

            // Scrape data from URLs concurrently
            List<CompletableFuture<ScrapedData>> futures = urls.stream()
                .limit(request.getMaxResults())
                .map(url -> CompletableFuture.supplyAsync(() -> scrapeUrl(url), executorService))
                .collect(Collectors.toList());

            // Wait for all scraping tasks to complete
            List<ScrapedData> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .map(data -> filterScrapedData(data, request.getFieldsToExtract()))
                .collect(Collectors.toList());

            long processingTime = System.currentTimeMillis() - startTime;

            return ScrapeResponse.builder()
                .searchTopic(request.getSearchTopic())
                .totalResults(urls.size())
                .successfulScrapes((int) results.stream().filter(r -> "success".equals(r.getStatus())).count())
                .failedScrapes((int) results.stream().filter(r -> "error".equals(r.getStatus())).count())
                .results(results)
                .metadata(Map.of(
                    "searchEngine", request.getSearchEngine(),
                    "language", request.getLanguage(),
                    "country", request.getCountry()
                ))
                .processingTime(processingTime)
                .status("completed")
                .message("Scraping completed successfully")
                .build();

        } catch (Exception e) {
            log.error("Error during web scraping: ", e);
            return ScrapeResponse.builder()
                .searchTopic(request.getSearchTopic())
                .totalResults(0)
                .successfulScrapes(0)
                .failedScrapes(0)
                .results(Collections.emptyList())
                .processingTime(System.currentTimeMillis() - startTime)
                .status("error")
                .message("Scraping failed: " + e.getMessage())
                .build();
        }
    }

    private List<String> getSearchResults(ScrapeRequest request) {
        // For demo purposes, we'll use a simple Google search simulation
        // In a real implementation, you would integrate with Google Custom Search API or SerpAPI
        return simulateGoogleSearch(request.getSearchTopic(), request.getMaxResults());
    }

    private List<String> simulateGoogleSearch(String query, int maxResults) {
        // Use real, reliable URLs for testing instead of fake ones
        List<String> urls = new ArrayList<>();
        
        // Base URLs that work well for testing
        List<String> baseUrls = Arrays.asList(
            "https://www.linkedin.com/company/",
            "https://github.com/topics/",
            "https://stackoverflow.com/questions/tagged/",
            "https://www.crunchbase.com/discover/organization.companies/",
            "https://www.glassdoor.com/Reviews/",
            "https://www.indeed.com/career-advice/finding-a-job/",
            "https://builtin.com/",
            "https://www.techrepublic.com/article/",
            "https://www.cio.com/article/",
            "https://www.zdnet.com/topic/",
            "https://www.microsoft.com",
            "https://www.google.com",
            "https://www.apple.com",
            "https://www.amazon.com",
            "https://www.meta.com",
            "https://www.netflix.com",
            "https://www.spotify.com",
            "https://www.uber.com",
            "https://www.airbnb.com",
            "https://www.slack.com",
            "https://www.dropbox.com",
            "https://www.salesforce.com",
            "https://www.adobe.com",
            "https://www.oracle.com",
            "https://www.ibm.com",
            "https://www.intel.com",
            "https://www.nvidia.com",
            "https://www.cisco.com",
            "https://www.hp.com",
            "https://www.dell.com"
        );
        
        // Generate URLs based on the query and maxResults
        String querySlug = query.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
        
        // Generate URLs dynamically to support any maxResults value
        int urlCount = 0;
        String[] suffixes = {"", "-companies", "-services", "-agencies", "-businesses", "-organizations", 
                           "-solutions", "-providers", "-experts", "-specialists", "-consultants", 
                           "-firms", "-enterprises", "-startups", "-ventures", "-directory", "-guide",
                           "-tips", "-reviews", "-news", "-blog", "-resources", "-tools", "-platforms"};
        
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] years = {"2024", "2023", "2022", "2021", "2020"};
        
        while (urlCount < maxResults) {
            for (String baseUrl : baseUrls) {
                if (urlCount >= maxResults) break;
                
                String suffix = suffixes[urlCount % suffixes.length];
                String number = numbers[urlCount % numbers.length];
                String year = years[urlCount % years.length];
                
                if (baseUrl.endsWith("/")) {
                    // For URLs that need a path, add the query with suffix
                    urls.add(baseUrl + querySlug + suffix);
                    urlCount++;
                    
                    // Add numbered variations
                    if (urlCount < maxResults) {
                        urls.add(baseUrl + querySlug + suffix + "-" + number);
                        urlCount++;
                    }
                    
                    // Add year variations
                    if (urlCount < maxResults) {
                        urls.add(baseUrl + querySlug + suffix + "-" + year);
                        urlCount++;
                    }
                    
                    // Add combined variations
                    if (urlCount < maxResults && !suffix.isEmpty()) {
                        urls.add(baseUrl + querySlug + suffix + "-" + number + "-" + year);
                        urlCount++;
                    }
                } else {
                    // For complete URLs, add them as-is
                    urls.add(baseUrl);
                    urlCount++;
                }
            }
        }
        
        // Remove duplicates and limit to maxResults
        return urls.stream()
            .distinct()
            .limit(maxResults)
            .collect(Collectors.toList());
    }

    private ScrapedData scrapeUrl(String url) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Add rate limiting
            Thread.sleep(rateLimitDelay);
            
            // Configure Jsoup connection with better error handling
            Document document = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(timeout)
                .followRedirects(true)
                .ignoreHttpErrors(true)  // Don't throw exceptions for HTTP errors
                .ignoreContentType(true) // Allow non-HTML content
                .maxBodySize(0) // No limit on body size
                .get();

            // Check if we got a valid response
            if (document == null) {
                throw new IOException("No document received from URL");
            }

            long responseTime = System.currentTimeMillis() - startTime;

            return ScrapedData.builder()
                .url(url)
                .title(extractTitle(document))
                .description(extractDescription(document))
                .emails(extractEmails(document))
                .phoneNumbers(extractPhoneNumbers(document))
                .socialLinks(extractSocialLinks(document))
                .content(extractContent(document))
                .domain(extractDomain(url))
                .status("success")
                .responseTime(responseTime)
                .build();

        } catch (IOException e) {
            log.warn("Failed to scrape URL {}: {}", url, e.getMessage());
            return ScrapedData.builder()
                .url(url)
                .status("error")
                .errorMessage("Connection error: " + e.getMessage())
                .responseTime(System.currentTimeMillis() - startTime)
                .build();
        } catch (Exception e) {
            log.error("Unexpected error scraping URL {}: ", url, e);
            return ScrapedData.builder()
                .url(url)
                .status("error")
                .errorMessage("Unexpected error: " + e.getMessage())
                .responseTime(System.currentTimeMillis() - startTime)
                .build();
        }
    }

    private String extractTitle(Document document) {
        Element titleElement = document.select("title").first();
        return titleElement != null ? titleElement.text().trim() : "";
    }

    private String extractDescription(Document document) {
        // Try meta description first
        Element metaDesc = document.select("meta[name=description]").first();
        if (metaDesc != null && !metaDesc.attr("content").isEmpty()) {
            return metaDesc.attr("content").trim();
        }

        // Try Open Graph description
        Element ogDesc = document.select("meta[property=og:description]").first();
        if (ogDesc != null && !ogDesc.attr("content").isEmpty()) {
            return ogDesc.attr("content").trim();
        }

        // Try Twitter description
        Element twitterDesc = document.select("meta[name=twitter:description]").first();
        if (twitterDesc != null && !twitterDesc.attr("content").isEmpty()) {
            return twitterDesc.attr("content").trim();
        }

        // Try to find a meaningful paragraph
        Elements paragraphs = document.select("p");
        for (Element p : paragraphs) {
            String text = p.text().trim();
            if (text.length() > 50 && text.length() < 300) {
                return text;
            }
        }

        // Fallback to first paragraph
        Element firstP = document.select("p").first();
        if (firstP != null) {
            String text = firstP.text().trim();
            return text.length() > 200 ? text.substring(0, 200) + "..." : text;
        }
        
        return "";
    }

    private Set<String> extractEmails(Document document) {
        Set<String> emails = new HashSet<>();
        String text = document.text();
        
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        while (matcher.find()) {
            String email = matcher.group().toLowerCase();
            // Filter out common false positives
            if (!isFalsePositiveEmail(email)) {
                emails.add(email);
            }
        }
        
        return emails;
    }

    private boolean isFalsePositiveEmail(String email) {
        // Filter out common false positives
        String lowerEmail = email.toLowerCase();
        return lowerEmail.contains("@2x") || 
               lowerEmail.contains("@3x") || 
               lowerEmail.contains("fallback") ||
               lowerEmail.contains(".min.") ||
               lowerEmail.endsWith(".js") ||
               lowerEmail.endsWith(".css") ||
               lowerEmail.matches(".*@\\d+\\.\\d+.*");
    }

    private Set<String> extractPhoneNumbers(Document document) {
        Set<String> phoneNumbers = new HashSet<>();
        String text = document.text();
        
        Matcher matcher = PHONE_PATTERN.matcher(text);
        while (matcher.find()) {
            phoneNumbers.add(matcher.group().trim());
        }
        
        return phoneNumbers;
    }

    private Set<String> extractSocialLinks(Document document) {
        Set<String> socialLinks = new HashSet<>();
        
        // Extract from href attributes
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            Matcher matcher = SOCIAL_PATTERN.matcher(href);
            if (matcher.find()) {
                socialLinks.add(href);
            }
        }
        
        // Extract from text content
        String text = document.text();
        Matcher matcher = SOCIAL_PATTERN.matcher(text);
        while (matcher.find()) {
            socialLinks.add(matcher.group());
        }
        
        return socialLinks;
    }

    private String extractContent(Document document) {
        // Remove script and style elements
        document.select("script, style, nav, header, footer, aside").remove();
        
        // Try multiple selectors for main content
        String[] contentSelectors = {
            "main", "article", ".content", ".post", ".entry", ".main-content",
            ".page-content", ".article-content", ".post-content", ".entry-content",
            "#content", "#main", "#article", ".container", ".wrapper"
        };
        
        for (String selector : contentSelectors) {
            Element content = document.select(selector).first();
            if (content != null && !content.text().trim().isEmpty()) {
                String text = content.text().trim();
                // Limit content length to avoid huge responses
                return text.length() > 2000 ? text.substring(0, 2000) + "..." : text;
            }
        }
        
        // Fallback to body content, but limit it
        if (document.body() != null) {
            String bodyText = document.body().text().trim();
            return bodyText.length() > 2000 ? bodyText.substring(0, 2000) + "..." : bodyText;
        }
        
        return "";
    }

    private String extractDomain(String url) {
        try {
            return java.net.URI.create(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Filters scraped data based on requested fields
     */
    private ScrapedData filterScrapedData(ScrapedData data, Set<String> fieldsToExtract) {
        if (fieldsToExtract == null || fieldsToExtract.isEmpty()) {
            return data; // Return all fields if none specified
        }

        ScrapedData.Builder builder = new ScrapedData.Builder()
            .url(data.getUrl())
            .status(data.getStatus())
            .responseTime(data.getResponseTime())
            .errorMessage(data.getErrorMessage());

        // Only include requested fields
        if (fieldsToExtract.contains("title")) {
            builder.title(data.getTitle());
        }
        if (fieldsToExtract.contains("description")) {
            builder.description(data.getDescription());
        }
        if (fieldsToExtract.contains("emails")) {
            builder.emails(data.getEmails());
        }
        if (fieldsToExtract.contains("phoneNumbers")) {
            builder.phoneNumbers(data.getPhoneNumbers());
        }
        if (fieldsToExtract.contains("socialLinks")) {
            builder.socialLinks(data.getSocialLinks());
        }
        if (fieldsToExtract.contains("content")) {
            builder.content(data.getContent());
        }
        if (fieldsToExtract.contains("domain")) {
            builder.domain(data.getDomain());
        }

        return builder.build();
    }

    /**
     * Converts scraped data to CSV format
     */
    public String convertToCsv(List<ScrapedData> results, Set<String> fieldsToExtract) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Create header row
            List<String> headers = new ArrayList<>();
            headers.add("URL");
            headers.add("Status");
            headers.add("Response Time (ms)");
            
            if (fieldsToExtract == null || fieldsToExtract.isEmpty()) {
                fieldsToExtract = Set.of("title", "description", "emails", "phoneNumbers", "socialLinks", "content", "domain");
            }
            
            if (fieldsToExtract.contains("title")) headers.add("Title");
            if (fieldsToExtract.contains("description")) headers.add("Description");
            if (fieldsToExtract.contains("emails")) headers.add("Emails");
            if (fieldsToExtract.contains("phoneNumbers")) headers.add("Phone Numbers");
            if (fieldsToExtract.contains("socialLinks")) headers.add("Social Links");
            if (fieldsToExtract.contains("content")) headers.add("Content");
            if (fieldsToExtract.contains("domain")) headers.add("Domain");
            if (fieldsToExtract.contains("errorMessage")) headers.add("Error Message");

            csvWriter.writeNext(headers.toArray(new String[0]));

            // Add data rows
            for (ScrapedData data : results) {
                List<String> row = new ArrayList<>();
                row.add(data.getUrl() != null ? data.getUrl() : "");
                row.add(data.getStatus() != null ? data.getStatus() : "");
                row.add(String.valueOf(data.getResponseTime()));
                
                if (fieldsToExtract.contains("title")) {
                    row.add(data.getTitle() != null ? data.getTitle() : "");
                }
                if (fieldsToExtract.contains("description")) {
                    row.add(data.getDescription() != null ? data.getDescription() : "");
                }
                if (fieldsToExtract.contains("emails")) {
                    row.add(data.getEmails() != null ? String.join("; ", data.getEmails()) : "");
                }
                if (fieldsToExtract.contains("phoneNumbers")) {
                    row.add(data.getPhoneNumbers() != null ? String.join("; ", data.getPhoneNumbers()) : "");
                }
                if (fieldsToExtract.contains("socialLinks")) {
                    row.add(data.getSocialLinks() != null ? String.join("; ", data.getSocialLinks()) : "");
                }
                if (fieldsToExtract.contains("content")) {
                    row.add(data.getContent() != null ? data.getContent().replaceAll("\\s+", " ").trim() : "");
                }
                if (fieldsToExtract.contains("domain")) {
                    row.add(data.getDomain() != null ? data.getDomain() : "");
                }
                if (fieldsToExtract.contains("errorMessage")) {
                    row.add(data.getErrorMessage() != null ? data.getErrorMessage() : "");
                }

                csvWriter.writeNext(row.toArray(new String[0]));
            }

            return writer.toString();
        } catch (Exception e) {
            log.error("Error converting data to CSV: ", e);
            throw new RuntimeException("Failed to convert data to CSV", e);
        }
    }
}
