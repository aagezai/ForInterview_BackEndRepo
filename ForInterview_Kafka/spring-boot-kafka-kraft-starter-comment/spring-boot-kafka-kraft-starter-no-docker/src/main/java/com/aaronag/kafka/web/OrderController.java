// =============================================
// OrderController.java
// =============================================
// This REST controller exposes HTTP endpoints to interact with Kafka.
//
// It allows external clients (like Postman, frontend apps, or other services)
// to send messages to Kafka topics through simple HTTP requests.
//
// Key Responsibilities:
//   ✅ Provide a health check endpoint (/ping)
//   ✅ Provide a POST endpoint (/produce) to send messages to Kafka
//   ✅ Use the OrderProducer service for transactional message delivery
//
// Typical flow:
//   POST /produce → send message → Kafka Producer → Topic → Kafka Consumer
// =============================================

package com.aaronag.kafka.web;

import com.aaronag.kafka.service.OrderProducer;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  // Marks this class as a REST controller (returns JSON, not views)
@RequiredArgsConstructor  // Lombok: auto-generates constructor for final fields
public class OrderController {

    // =====================================================
    // 1️⃣ Kafka Producer Dependency
    // =====================================================
    // Injects the OrderProducer service (defined in com.aaronag.kafka.service)
    // This is how the controller sends messages to Kafka.
    private final OrderProducer producer;

    // =====================================================
    // 2️⃣ Default Topic Injection
    // =====================================================
    // Reads the topic name from application.yml:
    // app.topics.orders: orders
    //
    // This acts as a default topic if no topic is provided by the user.
    @Value("${app.topics.orders}")
    String ordersTopic;

    // =====================================================
    // 3️⃣ Health Check Endpoint
    // =====================================================
    // GET /ping → responds with "ok"
    // Used to quickly verify that the service is up and running.
    @GetMapping("/ping")
    public String ping() {
        return "ok"; // Simple heartbeat endpoint
    }

    // =====================================================
    // 4️⃣ Kafka Produce Endpoint
    // =====================================================
    // POST /produce
    // Accepts a JSON request body to publish a message to Kafka.
    //
    // Example JSON:
    // {
    //   "topic": "orders",     // optional
    //   "key": "order-001",    // required
    //   "value": "CREATED"     // required
    // }
    //
    // If topic is not provided, defaults to ordersTopic.
    //
    // Returns HTTP 202 (Accepted) to indicate that the request
    // has been received and message publishing is in progress.
    // =====================================================
    @PostMapping("/produce")
    public ResponseEntity<?> produce(@RequestBody ProduceRequest req) {

        // Choose topic dynamically:
        // If user doesn't specify one, use default "orders" topic.
        String topic = (req.topic == null || req.topic.isBlank())
                ? ordersTopic
                : req.topic;

        // Delegate to the producer service, which sends message transactionally
        producer.sendTransactional(topic, req.key, req.value);

        // Return 202 Accepted (non-blocking acknowledgment)
        return ResponseEntity.accepted().build();
    }

    // =====================================================
    // 5️⃣ Inner Request DTO Class
    // =====================================================
    // Represents the expected JSON body structure for POST /produce.
    //
    // - Lombok @Data generates getters/setters, equals(), hashCode(), toString().
    // - @NotBlank ensures key and value fields cannot be null or empty.
    //
    // This class is static so it doesn’t require a reference to the outer class.
    // =====================================================
    @Data
    public static class ProduceRequest {
        // Optional topic override
        String topic;

        // Required key (used for partitioning and tracking messages)
        @NotBlank
        String key;

        // Required value (actual message payload)
        @NotBlank
        String value;
    }
}
