#!/bin/bash

# Test script for Web Scraper API
echo "🚀 Testing Web Scraper API..."

# Wait for service to start
echo "⏳ Waiting for service to start..."
sleep 5

# Test health endpoint
echo "🔍 Testing health endpoint..."
curl -s http://localhost:8080/api/scrape/health | jq '.' || echo "Health check failed"

echo ""
echo "📊 Testing service info endpoint..."
curl -s http://localhost:8080/api/scrape/info | jq '.' || echo "Info endpoint failed"

echo ""
echo "🌐 Testing web scraping endpoint..."
curl -X POST http://localhost:8080/api/scrape/web \
  -H "Content-Type: application/json" \
  -d '{
    "searchTopic": "software development companies",
    "maxResults": 3
  }' | jq '.' || echo "Scraping endpoint failed"

echo ""
echo "✅ API testing completed!"
