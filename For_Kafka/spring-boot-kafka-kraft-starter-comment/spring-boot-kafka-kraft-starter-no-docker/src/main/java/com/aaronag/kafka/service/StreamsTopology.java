// =============================================
// StreamsTopology.java
// =============================================
// This class defines the *Kafka Streams topology* — the flow
// of how messages move and are transformed between Kafka topics.
//
// In simple terms:
//   - It reads messages from an input topic ("orders")
//   - Transforms them (in this case, converts values to UPPERCASE)
//   - Sends the transformed messages to an output topic ("orders_enriched")
//
// Key points:
//  - Uses Spring’s Kafka Streams integration
//  - Registers a topology bean so Spring Boot can start it automatically
//  - Uses a functional style to build stream processing logic

package com.aaronag.kafka.service;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  // Marks this class as a configuration that defines stream processing beans
public class StreamsTopology {

    // =====================================================
    // Inject topic names from application.yml
    // =====================================================
    // Example in your config:
    // app:
    //   topics:
    //     orders: orders
    //     orders_enriched: orders.enriched
    //
    // These are the source (input) and destination (output) topics.
    @Value("${app.topics.orders}")
    String orders;

    @Value("${app.topics.orders_enriched}")
    String enriched;

    // =====================================================
    // Define the Kafka Streams topology bean
    // =====================================================
    // StreamsBuilder is the entry point for building Kafka Streams topologies.
    // A KStream represents a stream of key-value pairs (continuous data).
    //
    // When Spring Boot starts, it detects this bean and
    // automatically creates a Kafka Streams instance to execute it.
    // =====================================================
    @Bean
    public KStream<String, String> build(StreamsBuilder builder) {

        // Step 1️⃣: Create a KStream to read data from the "orders" topic.
        KStream<String, String> input = builder.stream(orders);

        // Step 2️⃣: Transform the stream — in this case, convert message values to uppercase.
        // mapValues() modifies the value but keeps the same key.
        // This is a stateless transformation.
        KStream<String, String> transformed = input.mapValues(v ->
                v == null ? null : v.toUpperCase()
        );

        // Step 3️⃣: Send the transformed messages to the "orders_enriched" topic.
        transformed.to(enriched);

        // Step 4️⃣: Return the input stream (optional).
        // Returning it allows further composition if other beans want to extend this topology.
        return input;
    }
}
