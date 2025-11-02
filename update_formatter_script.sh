#!/bin/bash

# Configuration
API_URL="http://localhost:8003/config/format"
AUTH0_TOKEN="YOUR_AUTH0_TOKEN_HERE"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Updating Formatter Configuration...${NC}"
echo ""

# Make the request
response=$(curl -s -w "\n%{http_code}" -X PUT "$API_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $AUTH0_TOKEN" \
  -d '{
    "rules": [
      {
        "name": "identifier_format",
        "value": "camel case"
      },
      {
        "name": "space_before_colon",
        "value": "true"
      },
      {
        "name": "space_after_colon",
        "value": "true"
      },
      {
        "name": "space_around_equals",
        "value": "true"
      },
      {
        "name": "newlines_before_println",
        "value": "1"
      }
    ]
  }')

# Extract HTTP status code and body
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

# Check response
if [ "$http_code" -eq 200 ]; then
  echo -e "${GREEN}✓ Success!${NC}"
  echo ""
  echo "Response:"
  echo "$body" | jq '.' 2>/dev/null || echo "$body"
else
  echo -e "${RED}✗ Failed with status code: $http_code${NC}"
  echo ""
  echo "Response:"
  echo "$body"
  exit 1
fi