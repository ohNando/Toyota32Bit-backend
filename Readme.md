# Toyota Backend Project

## Overview
The Toyota Backend Project is a microservice platform that collects and processes rate data from **TCP** and **REST** sources. The data is cached using **Hazelcast**, streamed via **Kafka**, and stored in a **PostgreSQL** database.

## Features
- **Data Collection**: Collects rate data from TCP and REST platforms.
- **Caching**: Uses **Hazelcast** for in-memory data for caching raw and calculated data.
- **Calculating** Uses **Grovy** for calculating data
- **Kafka Integration**: Streams data to Kafka topics.
- **Database**: Stores processed rate data in PostgreSQL.
- **Dynamic Subscriber Loading**: Loads and manages data subscribers dynamically.

## Technologies Used
- **Spring Boot**
- **Hazelcast** (for caching)
- **Kafka** (for message streaming)
- **PostgreSQL** (for data storage)
- **JPA/Hibernate** (for database interaction)

## Setup

### Prerequisites
- **Java 11+**
- **Maven/Gradle**
- **Kafka Broker** running
- **PostgreSQL Database**

### Clone the Repository
```bash
git clone https://github.com/your-username/toyota-backend-project.git
cd toyota-backend-project
```

### Configuration
- Update `application.properties` with your PostgreSQL and Kafka details.
- Configure Hazelcast and dynamic class loading paths as needed.

### Build and Run
To build the project:
```bash
mvn clean install
```
To run the project:
```bash
mvn spring-boot:run
```