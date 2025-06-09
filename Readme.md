# Toyota Backend Project

## Overview
The Toyota Backend Project is a microservice platform for collecting, processing, caching, and streaming currency rate data. The system integrates multiple components such as TCP and REST data sources, Kafka messaging, Hazelcast caching, PostgreSQL database, and more, all orchestrated via Docker containers.

## Features
- **Data Collection :** Collects rate data from TCP and REST platforms.
- **Caching :** Uses **Hazelcast** for in-memory data for caching raw and calculated data.
- **Calculating :** Uses **Grovy** for calculating data
- **Kafka Integration :** Streams rate data via Kafka topics.
- **Dynamic Subscriber Loading :** Loads and manages data subscribers dynamically.
- **Database :** Stores processed rate data in PostgreSQL.
- **Opensearch :** Used as a search and analytics engine for storing and querying rate data.
- **Logging and Monitoring :**
  - **Filebeat** collects and forwards logs from all services to Elasticsearch.
  - **Kibana** provides an interactive dashboard for log visualization and analysis.

## Technologies Used
- **Java 21**,**Spring Boot**
- **Hazelcast** (Caching)
- **Kafka** (Messaging)
- **PostgreSQL** (Database)
- **JPA/Hibernate** (ORM)
- **Groovy** (Calculations)
- **Docker & Docker Compose** (Container Architecture)
- **Elasticsearch, Kibana, OpenSearch** (Logging & monitoring)

## Setup

### Prerequisites
- **Docker Engine (20+)**
- **Docker Compose (v2+)**
- **(Optional)** **Java,Maven if building images manually**

### Getting Starting with Docker Compose

##### Clone the Repository
```bash
git clone https://github.com/your-username/toyota-backend-project.git
cd toyota-backend-project
```

#### Running the Complete System
- All microservices and dependencies (PostgreSQL, Kafka, Zookeeper, etc.) run as Docker containers via the included docker-compose.yml.
- First test your Docker:
```bash
docker --version
```
- Then Start the full stack with:
```bash
docker compose up --build # If you run it first time
```
```bash
docker compose up # After the first time you can use this command 
```

#### This command will:
- Launch `PostgreSQL` DB on port `5432`
- Start `Kafka Broker` & `Zookeper` `9092`,`2181`
- Launch `REST` and `TCP` platforms `8080`,`8081`
- Run `Main Platform` `8083`
- Start `Kafka consumer app`
- Run `ElasticSearch`, `Kibana` and `Filebeat` for logging and monitoring
- Run `Opensearch` and `Opensearch-Dashboard` for saving data and monitoring

### Stopping the System
- To stop all running containers:
```bash
docker compose down # or with '-v' flag
```

## Accesing Services
| Service              | URL / Port                                     |
| -------------------- | ---------------------------------------------- |
| Kafka UI Dashboard   | [http://localhost:8082](http://localhost:8082) |
| OpenSearch Dashboard | [http://localhost:5601](http://localhost:5601) |
| Elasticsearch        | [http://localhost:9201](http://localhost:9201) |
| Kibana Dashboard     | [http://localhost:5602](http://localhost:5602) |

### Configuration
- Update `application.properties` files inside each service for configurations.
- `Kafka`, `Hazelcast` and `database` connection properties are pre-configured for Docker networking.
- For custom configuration, modify relevant `application.properties` files and Docker environment variables in `docker-compose.yml`.

### Logs and Data Monitoring
- For monitoring, first run the services.
- Then wait a few moments for the services to initialize .
- Once ready, you can:
  - Access **Kibana** for log visualization at [http://localhost:5602](http://localhost:5602)
  - View application logs collected by **Filebeat** and stored in **ElasticSearch** use **OpenSearch Dashboard** at [http://localhost:5601](http://localhost:5601) to explore live rate data stored in **OpenSearch**


### **🔍 Step-by-Step Log Monitoring via Kibana**
1. **Open Kibana Dashboard**
   - Navigate to: [http://localhost:5602](http://localhost:5602)
2. **Create an Index Pattern**
    - Go to **“Stack Management” → “Index Patterns”** 
    - Click on **"Create index pattern"**
    - Enter the index name (e.g., `filebeat-*`)
    - Choose the timestamp field (usually `@Timestamp`)
    - Click **Create**
3. **Explore Logs**
    - Go to **"Discover"** 
    - Select the created index pattern
    - You should now see real-time logs streamed from your services
4. **Filter and Search**
    - Use Kibana's search bar to filter logs by container name, log level, message content, etc.
    - Example query:
        - ```pgsql
          container-name : "main-platform" AND log.level : "ERROR"```
### 📈 Rate Data Monitoring via OpenSearch Dashboard
1. **Open OpenSearch Dashboard**
    - Go to: [http://localhost:5601](http://localhost:5601)
2. **Log in with default credentials:**
    - **Username:** ********
    - **Password:** ********
3. **Create an Index Pattern**
    - Go to **“Stack Management” → “Index Patterns”**
    - Click on **"Create index pattern"**
    - Enter the index name (e.g., `rates*`)
    - Select `Timestamp` or `rateUpdateTime` as time field
    - Click **Create**
4. **Explore Data**
   - Go to **"Discover"** 
   - Select the created index pattern
   - You should now see real-time logs streamed from your services