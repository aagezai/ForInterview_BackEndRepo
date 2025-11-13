// =============================================
// OrderProducer.java
// =============================================
// This service is responsible for producing (sending)
// messages to Kafka topics.
//
// It uses Spring's KafkaTemplate — a high-level abstraction
// that handles serialization, connection pooling, retries,
// and transaction management for you.
//
// Key points:
//   ✅ Uses @Service to make it a Spring-managed bean
//   ✅ Injects KafkaTemplate via constructor (@RequiredArgsConstructor)
//   ✅ Sends messages inside a transaction for exactly-once delivery
// =============================================

package com.aaronag.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service  // Marks this class as a Spring-managed service component
@RequiredArgsConstructor  // Auto-generates a constructor for all final fields
public class OrderProducer {

    // =====================================================
    // KafkaTemplate
    // =====================================================
    // KafkaTemplate is the main class for sending messages to Kafka.
    // It handles serialization, buffering, retries, and transactions.
    //
    // The <String, String> type parameters mean:
    //   - Key is a String
    //   - Value is a String
    //
    // This template is auto-configured by Spring Boot using properties
    // defined in application.yml under spring.kafka.producer.*
    private final KafkaTemplate<String, String> template;

    // =====================================================
    // Send message transactionally
    // =====================================================
    // This method demonstrates *exactly-once* message delivery.
    //
    // It starts a Kafka transaction (because the template was configured
    // with a `transaction-id-prefix`), sends the record, and commits.
    //
    // If anything fails, the transaction automatically rolls back,
    // preventing partial writes or duplicates.
    //
    // Arguments:
    //   topic → destination Kafka topic
    //   key   → message key (used for partitioning)
    //   value → message payload (actual content)
    // =====================================================
    public void sendTransactional(String topic, String key, String value) {

        // `executeInTransaction` ensures all send operations inside this block
        // run as part of a single atomic transaction.
        //
        // If one message fails, all are rolled back.
        template.executeInTransaction(t -> {
            // The actual send operation — non-blocking (async)
            t.send(topic, key, value);

            // Return value isn't used here; just required by lambda signature
            return true;
        });
    }
}
