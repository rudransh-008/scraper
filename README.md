# Web Scraper Service

A Spring Boot REST API service for scraping web data including emails, phone numbers, social links, and descriptions from web pages based on search topics.

## Features

- **MVC Architecture**: Clean separation of concerns with Controller, Service, and Model layers
- **Concurrent Scraping**: Multi-threaded web scraping for better performance
- **Data Extraction**: Extracts emails, phone numbers, social media links, and content
- **Rate Limiting**: Built-in delays to respect website policies
- **Error Handling**: Comprehensive error handling and logging
- **RESTful API**: Clean REST endpoints for easy integration
- **Swagger/OpenAPI Documentation**: Interactive API documentation with Swagger UI

## Tech Stack

- **Spring Boot 3.2.0**
- **Java 17**
- **Jsoup** for HTML parsing
- **OkHttp** for HTTP requests
- **Lombok** for reducing boilerplate
- **SpringDoc OpenAPI** for Swagger documentation
- **Maven** for dependency management

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone and navigate to the project:**
   ```bash
   cd springboot-web-scraper
   ```

2. **Build the project:**
   ```bash
   mvn clean compile
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **The service will be available at:**
   ```
   http://localhost:8080/api
   ```

5. **Access Swagger UI for interactive API documentation:**
   ```
   http://localhost:8080/api/swagger-ui/index.html
   ```

6. **Access OpenAPI JSON specification:**
   ```
   http://localhost:8080/api/v3/api-docs
   ```


## API Endpoints

### 1. Scrape Web Data
**POST** `/api/scrape/web`

Scrapes web data based on a search topic.

**Request Body:**
```json
{
  "searchTopic": "software development companies",
  "maxResults": 10,
  "searchEngine": "google",
  "language": "en",
  "country": "us",
  "fieldsToExtract": ["emails", "phoneNumbers", "socialLinks", "title", "description"],
  "exportAsCsv": false
}
```

**Response:**
```json
{
  "searchTopic": "software development companies",
  "totalResults": 10,
  "successfulScrapes": 8,
  "failedScrapes": 2,
  "results": [
    {
      "url": "https://example.com/software-development",
      "title": "Software Development Company",
      "description": "Leading software development services...",
      "emails": ["contact@example.com", "info@example.com"],
      "phoneNumbers": ["+1-555-123-4567"],
      "socialLinks": ["https://linkedin.com/company/example"],
      "content": "Full page content...",
      "domain": "example.com",
      "status": "success",
      "responseTime": 1500
    }
  ],
  "metadata": {
    "searchEngine": "google",
    "language": "en",
    "country": "us"
  },
  "processingTime": 5000,
  "status": "completed",
  "message": "Scraping completed successfully"
  }
}
```

### 2. Health Check
**GET** `/api/scrape/health`

Returns the service health status.

### 3. Service Info
**GET** `/api/scrape/info`

Returns service information and available endpoints.

## Swagger/OpenAPI Documentation

This service includes comprehensive Swagger/OpenAPI documentation for easy API exploration and testing.

### Accessing Swagger UI

Once the application is running, you can access the interactive Swagger UI at:
```
http://localhost:8080/api/swagger-ui/index.html
```

### Features of Swagger Documentation

- **Interactive API Testing**: Test all endpoints directly from the browser
- **Request/Response Examples**: See example requests and responses for each endpoint
- **Schema Documentation**: Detailed documentation of all request/response models
- **Parameter Validation**: Built-in validation rules and constraints
- **Error Response Documentation**: Comprehensive error response documentation

### API Documentation Structure

The Swagger documentation includes:

1. **Web Scraping Endpoints**
   - POST `/api/scrape/web` - Main scraping endpoint with detailed examples
   - GET `/api/scrape/health` - Health check endpoint
   - GET `/api/scrape/info` - Service information endpoint

2. **Data Models**
   - `ScrapeRequest` - Input model with validation rules
   - `ScrapeResponse` - Response model with detailed field descriptions
   - `ScrapedData` - Individual scraped data model

3. **Response Examples**
   - Success responses with sample data
   - Error responses with error codes and messages
   - Validation error examples

### OpenAPI Specification

The OpenAPI 3.0 specification is available at:
```
http://localhost:8080/api/v3/api-docs
```

This can be used to:
- Generate client SDKs in various languages
- Import into API testing tools like Postman
- Generate documentation in other formats

## Configuration

The service can be configured via `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

scraper:
  user-agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
  timeout: 10000
  max-retries: 3
  rate-limit-delay: 1000
```

## Example Usage

### Using cURL

```bash
# Scrape data for a specific topic
curl -X POST http://localhost:8080/api/scrape/web \
  -H "Content-Type: application/json" \
  -d '{
    "searchTopic": "marketing agencies",
    "maxResults": 5
  }'

# Scrape only emails and export as CSV
curl -X POST http://localhost:8080/api/scrape/web \
  -H "Content-Type: application/json" \
  -d '{
    "searchTopic": "contact us",
    "maxResults": 3,
    "fieldsToExtract": ["emails"],
    "exportAsCsv": true
  }' -o emails.csv

# Scrape specific fields only
curl -X POST http://localhost:8080/api/scrape/web \
  -H "Content-Type: application/json" \
  -d '{
    "searchTopic": "software companies",
    "maxResults": 5,
    "fieldsToExtract": ["emails", "phoneNumbers", "socialLinks"]
  }'
```

### Using JavaScript/Fetch

```javascript
const response = await fetch('http://localhost:8080/api/scrape/web', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    searchTopic: 'digital marketing companies',
    maxResults: 10
  })
});

const data = await response.json();
console.log(data);
```

## Data Extraction

The service extracts the following types of data:

- **Emails**: Valid email addresses using regex pattern matching
- **Phone Numbers**: US phone number formats
- **Social Links**: Instagram, Twitter, Facebook, LinkedIn, YouTube links
- **Content**: Main page content (excluding scripts and styles)
- **Metadata**: Title, description, domain information

### Field Selection

You can specify which fields to extract using the `fieldsToExtract` parameter:

```json
{
  "fieldsToExtract": ["emails", "phoneNumbers", "socialLinks"]
}
```

Available fields:
- `emails` - Email addresses found on the page
- `phoneNumbers` - Phone numbers found on the page
- `socialLinks` - Social media links found on the page
- `title` - Page title
- `description` - Page description or meta description
- `content` - Main page content
- `domain` - Domain name of the URL

### CSV Export

You can export scraped data as a CSV file by setting `exportAsCsv: true`:

```json
{
  "searchTopic": "contact us",
  "fieldsToExtract": ["emails", "phoneNumbers"],
  "exportAsCsv": true
}
```

The CSV file will be automatically downloaded with a timestamped filename like `scraped_data_1694678400000.csv`.

## Error Handling

The service includes comprehensive error handling:

- **Network Errors**: Timeout and connection issues
- **Parsing Errors**: HTML parsing failures
- **Rate Limiting**: Built-in delays between requests
- **Validation**: Input validation for request parameters

## Performance

- **Concurrent Processing**: Uses thread pool for parallel scraping
- **Rate Limiting**: Configurable delays to avoid overwhelming target sites
- **Timeout Management**: Configurable timeouts for HTTP requests
- **Memory Efficient**: Streams data processing to handle large responses

## Development

### Project Structure

```
src/main/java/com/scraper/
├── controller/          # REST controllers
├── service/            # Business logic
├── model/              # Data models
├── config/             # Configuration classes
└── WebScraperApplication.java
```

### Building and Testing

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn clean package

# Run JAR
java -jar target/web-scraper-1.0.0.jar
```

## License

This project is licensed under the MIT License.
# scraper
# scraper
