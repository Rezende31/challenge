# Challenge - CEP Lookup Service

Spring Boot (Java 17+) app to fetch CEP from external API (mocked with Mockoon) and persist query logs in H2 database.

## Requirements
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

## Start infra (DB + mocks)
```bash
docker compose up -d
```
- Postgres: localhost:5432 (db/user/pass: challenge)
- Mockoon: http://localhost:8081 (ex: GET /cep/01001000)

## Configuration
Default profile: `local` (uses H2 + Mockoon). 
- `local`: H2 in-memory + Mockoon API
- `dev`: Postgres + Mockoon API

See `src/main/resources/application.yml`.

## Run the app
```bash
./mvnw spring-boot:run
```

## Test the endpoint
```bash
curl http://localhost:8080/api/ceps/01001000
```

## Implementation features
- **SOLID principles**: Service layer separation, dependency injection
- **Logging**: All CEP queries are logged to database with timestamp and response time
- **External API**: Mockoon simulates CEP API
- **Database**: PostgreSQL with JPA/Hibernate
- **Clean code**: Lombok annotations, proper package structure

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   Spring Boot   │    │   H2 Database   │
│   (Browser/     │───▶│   Application   │───▶│   (Query Logs)  │
│   Postman)      │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Mockoon API   │
                       │   (Port 8081)   │
                       └─────────────────┘

Flow:
1. GET /api/ceps/{cep} → CepController
2. CepController → CepService
3. CepService → CepClient (mock API)
4. CepService → QueryLogRepository (save log)
5. Response with CEP data + audit trail
```

**Components:**
- `CepController`: REST endpoint (`GET /api/ceps/{cep}`)
- `CepService`: Business logic and logging (SOLID principles)
- `CepClient`: External API integration (Mockoon)
- `QueryLog`: Entity for audit trail (timestamp, response time, success)
- `QueryLogRepository`: Data access layer (JPA)

## Presentation (15 min)
- Solution diagram (layers, flow, persistence)
- Walk through key code
- Start containers, run app, call endpoint
- Show logs saved in Postgres
