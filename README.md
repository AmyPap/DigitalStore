
#  Digital Store

API for a digital store project developed as a **group assignment** using **Spring Boot**.  
I was initially responsible for the **database design**, and later contributed to parts of the **backend architecture**,  
the **implementation logic**.


This **README** was created later as part of my own effort to provide a clearer and more complete presentation of the project.

##  Prerequisites

Before running the project, make sure you have:

- **Java 23** installed
- **MariaDB** installed and running locally
- (Optional) **IntelliJ IDEA** or another IDE that supports Maven projects
- Internet connection (for Maven to download dependencies)


## Run with Maven Wrapper

This project uses the **Maven Wrapper**, so you don't need to install Maven manually.  
The project is built using **Spring Boot 3.4.1** and **Java 23**.

Main Dependencies
- spring-boot-starter-web – RESTful API development
- spring-boot-starter-data-jpa – database access using JPA/Hibernate
- mariadb-java-client – MariaDB database driver
- springdoc-openapi-starter-webmvc-ui – Swagger UI for API documentation
- spring-boot-starter-test – unit and integration testing

Simply run the following command to build and start the application:
```bash
./mvnw spring-boot:run
```
Maven will automatically download the correct version (3.9.9) as defined in the wrapper configuration.

Swagger UI will be available at:
http://localhost:8080/swagger-ui.html

## CORS Configuration

The project includes a WebConfig class implementing WebMvcConfigurer to allow CORS requests for development.
This enables local front-end clients ( running on ports 8080–8081) to access backend APIs without CORS restrictions.

## Database Setup

This project uses **MariaDB** as the database.
Make sure you have MariaDB installed and running locally!

Create a new database with command:
```sql
CREATE DATABASE intersport_test;
```
Update your database credentials in

`src/main/resources/application.properties`

**Note**: Change the *username* and *password* to match your local MariaDB configuration!

After setting up the database, you can import the schema and sample data from:

`src/main/resources/database.sql`
 
## Project Architecture

The project follows a layered architecture built with **Spring Boot, JPA, and MariaDB**.
Each package has a clear responsibility and corresponds to a specific layer in the backend structure.

### Description of Layers

- **Controller Layer:**
Handles incoming HTTP requests and defines the REST endpoints for each resource (Products, Brands, Sizes, etc.).
- **Repository Layer:**
Manages persistence using Spring Data JPA,connects the service layer to the MariaDB database.
- **Model Layer:**
Defines entity classes that map directly to tables in the database (Products, Brands, Categories, etc.).
- **DTO Layer:**
Ensures clean data transfer between backend layers and the frontend without exposing entities directly.
- **Service Layer:**
Contains the business logic of the application.
ProductService validates data (e.g., stock, color, size) and interacts with repositories.
Custom exceptions like NegativePriceException ensure safe data handling.
- **Config Layer:**
Includes configuration classes like WebConfig for CORS and Swagger setup.

## Data Flow Example

A client (frontend or API tester) sends a request to an endpoint.
The ProductController receives the request and converts JSON into a DTO object.
The DTO is passed to the ProductService, which validates input data.
The ProductService communicates with the ProductsRepository to save the data.
The database (MariaDB) stores the new product record and a response (success or error) is returned back to the client.