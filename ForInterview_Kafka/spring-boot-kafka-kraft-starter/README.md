# Spring Boot + Kafka (KRaft) Starter

## Prereqs
- Docker + Docker Compose
- Java 21
- Maven 3.9+

## Run Kafka (KRaft single node)
```bash
docker compose up -d
```

## Build & run app
```bash
mvn spring-boot:run
```

## Test: send a message
```bash
curl -X POST localhost:8080/api/orders?tx=true   -H 'Content-Type: application/json'   -d '{"key":"ord-1","value":"order received"}'
```

- Consumer logs the record.
- Kafka Streams writes to `orders.enriched` topic.
- Errors route to `${app.topics.orders_dlq}` via DefaultErrorHandler.
