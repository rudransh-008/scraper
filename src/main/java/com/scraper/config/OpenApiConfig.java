package com.scraper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Web Scraper Service API")
                        .description("A comprehensive Spring Boot REST API for web scraping operations. " +
                                "This service allows you to scrape web data including emails, phone numbers, " +
                                "social media links, and content from websites based on search topics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Web Scraper Team")
                                .email("support@webscraper.com")
                                .url("https://github.com/your-org/web-scraper"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.webscraper.com")
                                .description("Production Server")
                ));
    }
}
