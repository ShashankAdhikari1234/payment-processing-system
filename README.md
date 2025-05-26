# Payment Processing System

## Overview
This Spring Boot project integrates PayPal's REST API to create. It includes JWT security, webhook handling, resilience patterns, and persistent transaction storage in PostgreSQL.

## Features
- OAuth2 token management for PayPal API using keycloak
- Payment creation
- PostgreSQL transaction tracking (success & failure)
- Retry mechanism for network failures
- Webhook endpoint with HMAC validation
- Circuit breaker with Resilience4j
- Dockerized for easy deployment

## Setup Instructions

1. Clone repository
2. Configure `application.yml` with your PayPal credentials and webhook secret
3. Run PostgreSQL (e.g., via Docker Compose)
4. Build and run the app:

```bash
./mvnw clean package
java -jar target/paypal-integration-1.0.0.jar