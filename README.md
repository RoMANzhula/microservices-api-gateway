# Microservices Architecture

## Project Structure
```
├── api-gateway
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
├── eureka-services-registr-service
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
├── expenses-service
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
├── journal-service
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
├── microservices-api-gateway.iml
├── services-config-server
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
├── user-service
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src
│   └── target
└── wallet-service
    ├── HELP.md
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    ├── src
    └── target
```

## Overview
This project is a microservices-based architecture built with Spring Boot and Spring Cloud. It includes multiple services that communicate with each other via REST API using WebClient. The system utilizes PostgreSQL as the database and Eureka for service discovery.

## Technologies Used
- **Spring Boot 3.4.2**
- **Spring Cloud 2024.0.0**
- **Spring WebFlux (WebClient)**
- **Spring Cloud Netflix Eureka**
- **Spring Cloud Config Server**
- **PostgreSQL**
- **Maven**
- **Lombok**

## Microservices Description

### 1. **API Gateway** (`api-gateway`)
- Acts as a single entry point for all requests.
- Routes incoming requests to the appropriate services.
- Load balancing and security handling.

### 2. **Eureka Service Registry** (`eureka-services-registr-service`)
- Handles service discovery for all microservices.
- Registers and manages service instances dynamically.

### 3. **Expenses Service** (`expenses-service`)
- Manages expenses-related operations.
- Stores expense records in PostgreSQL.
- Provides RESTful APIs for CRUD operations.

### 4. **Journal Service** (`journal-service`)
- Handles journal entries and logs.
- Stores data in PostgreSQL.

### 5. **Config Server** (`services-config-server`)
- Centralized configuration management.
- Loads configurations dynamically from a remote repository.

### 6. **User Service** (`user-service`)
- Manages user authentication and roles.
- Stores user details in PostgreSQL.

### 7. **Wallet Service** (`wallet-service`)
- Handles wallet transactions and balance management.
- Provides API endpoints for retrieving and updating balances.

## How to Run on localhost

1. Run EurekaServicesRegistrServiceApplication
2. Run ServicesConfigServerApplication
3. Run ApiGatewayApplication
4. Run services (and wait 5 minutes before testing):
    - UserServiceApplication
    - WalletServiceApplication
    - ExpensesServiceApplication
    - JournalServiceApplication**

 **ports:**
    - 8765 - api-gateway
    - 8081 - user-service
    - 8082 - wallet-service
    - 8083 - journal-service
    - 8084 - expenses-service
    - 8086 - services-config-server AND CONFIG_PORT   


## Configuration
The application retrieves configurations from the Config Server. Ensure that the `application.yml` or `bootstrap.yml` files are properly set up to connect to the Config Server.

## Database Setup
1. Install PostgreSQL and create required databases for each service (or you can use one common DB).
2. Update the `application.properties` or `application.yml` in each service with the correct database credentials.
3. Run database migrations (if applicable).

## API Communication
All services communicate via REST API using WebClient. API calls are secured and routed through the API Gateway.

## License
This project is licensed under the MIT License.

