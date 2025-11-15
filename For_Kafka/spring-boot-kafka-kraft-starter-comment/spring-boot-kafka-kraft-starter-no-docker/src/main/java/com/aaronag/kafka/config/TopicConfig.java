// =============================================
// TopicConfig.java
// =============================================
// This configuration class automatically creates Kafka topics when
// the Spring Boot application starts (if auto-topic-creation is enabled).
//
// It uses Spring Kafka’s AdminClient (auto-configured by Spring Boot)
// to ensure topics exist before producers or consumers start.
//
// Each @Bean method returns a NewTopic object, representing a Kafka topic
// with a specified name, number of partitions, and replication factor.
//
// Key points:
//  - Helps avoid "Topic does not exist" errors
//  - Creates topics programmatically at startup
//  - Makes your app more portable (no manual CLI topic creation needed)

package com.aaronag.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // Marks this as a Spring configuration class
public class TopicConfig {

    // =====================================================
    // Topic Names (injected from application.yml)
    // =====================================================
    // These topic names come from your configuration file.
    // Example:
    // app:
    //   topics:
    //     orders: orders
    //     orders_dlq: orders.dlq
    //     orders_enriched: orders.enriched

    @Value("${app.topics.orders}")
    String orders;

    @Value("${app.topics.orders_dlq}")
    String dlq;

    @Value("${app.topics.orders_enriched}")
    String enriched;

    // =====================================================
    // Define Topics as Spring Beans
    // =====================================================
    // Each method below returns a NewTopic object that describes
    // a Kafka topic to be created (if it doesn’t already exist).
    //
    // Parameters of NewTopic:
    //   1. name — topic name
    //   2. numPartitions — how many partitions (parallelism)
    //   3. replicationFactor — how many replicas of each partition
    //
    // Note: replicationFactor=1 means no redundancy (suitable for local dev).
    // =====================================================

    // Topic for raw "orders" messages
    @Bean
    NewTopic ordersTopic() {
        return new NewTopic(orders, 3, (short) 1);
    }

    // Dead Letter Queue (DLQ) topic for failed messages
    @Bean
    NewTopic dlqTopic() {
        return new NewTopic(dlq, 3, (short) 1);
    }

    // Enriched topic for processed or transformed messages
    @Bean
    NewTopic enrichedTopic() {
        return new NewTopic(enriched, 3, (short) 1);
    }
}
