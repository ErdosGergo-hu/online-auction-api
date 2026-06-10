# 🔨 Online Auction API

A RESTful backend for an online auction platform built with **Spring Boot 4**, **Java 25**, and **PostgreSQL**. Handles user authentication, auction management, bidding, and dynamic search filtering.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Framework    | Spring Boot 4                       |
| Language     | Java 25                             |
| Database     | PostgreSQL (prod) · H2 (dev)        |
| Migrations   | Liquibase                           |
| Security     | Spring Security + JWT (JJWT 0.13)   |
| Mapping      | MapStruct                           |
| Docs         | SpringDoc OpenAPI (Swagger UI)      |
| Build        | Maven                               |
| Container    | Docker + Docker Compose             |

---

## Features

- JWT authentication — register, login, token refresh, logout
- Auction CRUD — create, update, close auctions
- Bidding — place bids, track current highest bidder
- Dynamic filtering — filter auctions by category, condition, status, location, price range via `DynamicSpecification`
- User stats — active bids, auctions won/sold, total spent/earned via DB view
- Paginated search results with sorting
- Swagger UI for API exploration

---

## Getting Started

### 1. Run the application

The application can run with PostgreSQL or H2 Database, depends on the application profile.

#### 1.1 PostgreSQL

If you run the application with PostgreSQL then you can start it with separately:

```bash
  docker compose up postgres -d
  ./mvnw spring-boot:run
```

or you can run it with the Spring app itself in Docker (this will run everything together):

```bash
  docker compose up -d
```

This starts a PostgreSQL 16 container on port `5432`:

#### 1.2 H2

H2 database runs with the app, you don't need to run it separately, just set the profile to: h2

```bash
./mvnw spring-boot:run
```

The API starts on **http://localhost:8080**.

### 2. Explore the API

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Environment Variables

Configure these in `application.properties` or as environment variables:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/auction_db
spring.datasource.username=auction_user
spring.datasource.password=auction_pass

# JWT
jwt.secret=your-secret-key-min-32-characters
jwt.access-token-expiry=900000     # 15 minutes
jwt.refresh-token-expiry=604800000 # 7 days
```

---

## API Overview

### Auth

| Method | Endpoint              | Auth     | Description          |
|--------|-----------------------|----------|----------------------|
| POST   | `/api/auth/register`  | Public   | Register new user    |
| POST   | `/api/auth/login`     | Public   | Login, get tokens    |
| POST   | `/api/auth/refresh`   | Public   | Refresh access token |
| POST   | `/api/auth/logout`    | Bearer   | Invalidate token     |

### Auctions

| Method | Endpoint                  | Auth   | Description              |
|--------|---------------------------|--------|--------------------------|
| POST   | `/api/auctions/search`    | Public | Dynamic filtered search  |
| GET    | `/api/auctions/{id}`      | Public | Get auction by ID        |
| POST   | `/api/auctions`           | Bearer | Create auction           |
| PUT    | `/api/auctions/{id}`      | Bearer | Update auction           |
| DELETE | `/api/auctions/{id}`      | Bearer | Delete auction           |

### Search request body example

```json
{
  "query": "iphone",
  "categories": ["ELECTRONICS"],
  "conditions": ["NEW", "EXCELLENT"],
  "statuses": ["ACTIVE"],
  "minBid": 10000,
  "maxBid": 500000,
  "page": 0,
  "size": 20,
  "sortBy": "endDateTime",
  "sortDir": "asc"
}
```

Any field can be omitted — omitted means no filter on that field.

---

## Database Migrations

Schema is managed by **Liquibase**. Migration files live in:

```
src/main/resources/db/changelog/
```

Migrations run automatically on startup. To add a new migration, create a new changeset file and reference it in `db.changelog-master.yaml`.

---

## Project Structure

```
src/main/java/hu/erdosgergo/online_auction_api/
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # JPA repositories
├── entity/           # JPA entities
├── dto/              # Request / response DTOs
├── specification/    # Dynamic JPA Specification (DynamicSpecification)
├── security/         # JWT filter, SecurityConfig
├── mapper/           # MapStruct mappers
└── exception/        # Global exception handler
```