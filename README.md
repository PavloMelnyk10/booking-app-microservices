# Accommodation Booking Service - Microservices Architecture

## Project Description
This project is a microservices-based platform for managing accommodation bookings. It provides functionality for managing properties, booking accommodations, handling payments, and user authentication. The system is built using a microservices architecture to ensure scalability, resilience, and maintainability.

Project is a migration of my original monolithic project, [Accommodation booking app](https://github.com/PavloMelnyk10/booking-app), to a microservices architecture using an extended technology stack. The main reasons for migrating to microservices include:

- **Scalability**: Each microservice can be scaled independently based on its specific load and performance requirements.
- **Resilience**: Failure in one microservice does not impact the entire system, allowing for better fault isolation and recovery.
- **Maintainability**: Smaller, modular services are easier to manage, develop, and deploy independently.
- **Flexibility**: Different technologies and frameworks can be used for different services, optimizing for the best tools for each job.

## Architecture Overview
The application is divided into several independent microservices:

- **API Gateway**: Entry point for all client requests, handles routing and cross-cutting concerns.
- **Discovery Service**: Eureka Server for service registration and discovery.
- **User Service**: Handles user management and profile operations.
- **Accommodation Service**: Manages property listings and details.
- **Booking Service**: Processes booking requests and manages booking lifecycle.
- **Payment Service**: Integrates with Stripe for payment processing.
- **Notification Service**: Sends notifications via Telegram API.

## Technologies Used
- **Microservices Framework**: Spring Boot.
- **Service Discovery**: Netflix Eureka.
- **API Gateway**: Spring Cloud Gateway.
- **Authentication & Authorization**: Keycloak.
- **Communication**:
    - Synchronous: Spring Cloud OpenFeign.
    - Asynchronous: Apache Kafka.
- **Database**: PostgreSQL
- **Payment Processing**: Stripe API.
- **Notifications**: Telegram Bot API.
- **Build Tool**: Maven.
- **Containerization**: Docker, Docker Compose.
- **Documentation**: Swagger/OpenAPI.

## Service Communication
### Synchronous Communication
- REST APIs via Feign Client for direct service-to-service communication.
- Used for operations requiring immediate responses.

### Asynchronous Communication
- Event-driven architecture using Kafka.
- Services publish events that other services can consume.
- Used for operations that don't require immediate responses(sending notifications, updating completed bookings counter, etc).

## Authentication with Keycloak
The application uses Keycloak for authentication and authorization:
- OAuth2/OpenID Connect protocols.
- Role-based access control.
- Single Sign-On capabilities.
- Token-based authentication.

## Monitoring and Observability
The application includes:
- Distributed tracing.
- Centralized logging.
- Health checks and metrics.

## API Documentation
Each microservice exposes its API documentation via Swagger/OpenAPI, accessible through the API Gateway.

## Microservices Breakdown

### User Service
- Manages user registration and authentication via Keycloak.
- Handles user profiles and preferences.
- Manages user roles and permissions.

### Accommodation Service
- Manages property listings and details.
- Handles search and filtering of accommodations.
- Provides accommodation availability information.

### Booking Service
- Processes booking requests.
- Manages booking lifecycle (creation, confirmation, completion, cancellation).
- Implements the loyalty discount system based on user booking history.

### Payment Service
- Integrates with Stripe for payment processing.
- Manages payment sessions and status updates.
- Handles payment expiration and reconciliation.

### Notification Service
- Sends real-time notifications via Telegram.
- Organizes notifications by topics (Bookings, Payments, Accommodations).
- Provides status updates to administrators.