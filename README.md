# Brokage Firm API
A RESTful API for a brokage firm application that allows management of customer orders, assets, and trading operations.

This application provides a complete backend solution for a brokerage firm with:
* Role-based access control (Admin, Employee, Customer)
* Order management (creation, matching, cancellation)
* Asset tracking and management 
* Search capabilities

## How to Run
* Clone this [repository](https://github.com/erdemiryigit/brokage-firm.git)
```bash
git clone https://github.com/erdemiryigit/brokage-firm.git
```
If you have docker installed, you can run the application using Docker.
```bash
docker-compose up
```

This application is packaged as a jar. You run it using the ```java -jar``` command.

* Make sure you are using JDK 21 and Maven 3.x
* Make sure you have a PostgreSQL database running on port 5432. You can use Docker to run a PostgreSQL container.
* You can run the tests and build the project with commands
* ```mvn test```
* ```mvn clean package -DskipTests```
* Once successfully built, you can run the service using the following commands:
* ```mvn spring-boot:run```
* or
* ```java -jar target/brokage-firm.jar```


* The application will start on port 8081 by default. You can change the port by modifying the `application.properties` file in the `src/main/resources` directory. 
* You should see something like this in the console logs on application startup:
```
2025-04-02 06:23:41 - Tomcat started on port 8081 (http) with context path '/brokage-firm'
2025-04-02 06:23:41 - Started BrokageFirmApplication in 6.793 seconds (process running for 7.659)
```

## Tech Stack
* Java 21
* Spring Boot 
* H2/PostgreSQL (depending on configuration)
* Swagger/OpenAPI for API documentation 
* Maven 3.x for dependency management
* JUnit5 for testing
* Docker for containerization

## Getting Started
### Prerequisites
- JDK 21
- Maven 3.x
- Git
- Docker & Docker Compose

# Security
The API uses Basic Authentication with role-based access control. All endpoints require authentication and appropriate role authorization.

User Credentials (All passwords match the usernames for testing purposes)
* Admin:username=admin password=admin
* Employee: username=employee password=employee
* Customers: username=customer1 password=customer1, username=customer2 password=customer2
* Admin can access all endpoints
* Employee can access order creation and management endpoints
* Customers can only access their own orders and assets
* All endpoints are secured with Basic Authentication

## Testing the project
### Unit Tests
Use below command to test the project 

    $ mvn test

# API Testing Strategy
You can test the API using Postman or Swagger UI. The API is designed to be RESTful and follows standard HTTP methods (GET, POST, PUT, DELETE) for CRUD operations.

Postman collection can be found in the `postman` directory. It contains various tests for different endpoints.

## API Testing with Postman
* Postman collection with sequential tests
* Order creation tests 
* Order retrieval tests
* Admin operations 
* Cancellation operations 
* Asset management operations

## API Testing with Swagger
* Swagger UI for manual testing
* Example data for various endpoints

### Browser URL
Following URL can be used for Swagger UI (giving REST interface details):
http://localhost:8081/brokage-firm/swagger-ui/index.html or just
click [here](http://localhost:8081/brokage-firm/swagger-ui/index.html)

# Features
## Locking
* Database Locking and Concurrency Control implemented.
* Global locking mechanism via a dedicated Lock table. 
* Each lock identified uniquely (UUID). 
* Optimistic locking via version field in entities.

# Asset Management
* Track customer assets (stocks and TRY currency)
* Monitor total and usable balances
* Search assets with filtering
# Role-based Access Control
* Admin: Can match orders and access all data 
* Employee: Can create and manage orders on behalf of customers 
* Customer: Can only view and manage their own orders and assets

## Dependencies
* MapStruct
* Lombok
* Apache Commons Collections4

## Improvements to be made
* Token-Based Authentication: Replace Basic Auth with JWT tokens 
* User Management Endpoints: Add endpoints for user creation/suspension 
* Full Multi-Currency Support: Support for trading between any currency pairs 
* Scheduled Tasks: Background processes for order auto-cancellation after timeout
* Asset class with subclasses for different asset types (e.g., Stock, Bond, etc.)
* Spring profiles for different environments (dev, test, prod)

## License
[MIT](https://choosealicense.com/licenses/mit/)