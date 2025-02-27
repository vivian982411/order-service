# Order Service

## 📌 Description

The order-service is a microservice responsible for managing customer orders in an e-commerce platform. It allows creating, updating, retrieving, and managing order statuses while integrating with Apache Kafka to notify other services of order status changes.

## 🛠️ Tech Stack

Java 17

Spring Boot 3 (Spring Web, Spring Data JPA, Lombok)

MySQL 8 (Database)

Apache Kafka (Messaging)

Docker & Docker Compose

JUnit 5 (Unit Testing)

## 🚀 Getting Started

🔹 Prerequisites

Ensure you have the following installed:

Java 17

Maven

Docker & Docker Compose

MySQL 8

### 🔹 Clone the Repository

git clone https://github.com/your-org/order-service.git
cd order-service

### 🔹 Configure the Application

Modify src/main/resources/application.yml to match your database and Kafka settings:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/orders_db
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

kafka:
  bootstrap-servers: localhost:9092

### 🔹 Run with Docker Compose (Recommended)

Start dependencies (MySQL & Kafka):

docker-compose up -d

### 🔹 Run the Application

Using Maven:

./mvnw spring-boot:run

## 📡 API Endpoints

### 🔹 Create Order

POST /orders?customerId=12345

Response:

{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "customerId": "12345",
  "status": "PROCESSING",
  "createdAt": "2024-02-25T10:00:00",
  "updatedAt": "2024-02-25T10:00:00"
}

### 🔹 Get Order by ID

GET /orders/{id}

### 🔹 Get All Orders

GET /orders

### 🔹 Update Order Status

PUT /orders/{id}/status?status=SHIPPED

## 🧪 Running Tests

Run unit tests using:

./mvnw test

## 📦 Deployment

### 🔹 Build the JAR

./mvnw clean package

### 🔹 Run with Docker

docker build -t order-service .
docker run -p 8081:8081 order-service

## 🖥️ Architecture Diagram

Below is a diagram showing the interaction between the order-service, Kafka, MySQL, and Docker:

graph LR;
    User -->|Creates Order| OrderService;
    OrderService -->|Saves to| MySQL;
    OrderService -->|Publishes Event| Kafka;
    Kafka -->|Consumes Event| NotificationService;
    NotificationService -->|Sends Notification| User;

    subgraph Docker Containers
        MySQL
        Kafka
        Zookeeper
        OrderService
        NotificationService
    end
