// =============================================
// KafkaStreamsConfig.java
// =============================================
// This configuration class sets up Kafka Streams integration in Spring Boot.
//
// It provides a bean of type KafkaStreamsConfiguration that tells Spring Kafka
// how to initialize and manage Kafka Streams applications.
//
// Key points:
//  - Configures bootstrap servers (Kafka broker address)
//  - Sets application.id (unique identifier for the stream app)
//  - Enables exactly-once processing (guaranteed delivery semantics)
//
// Once this bean is defined, Spring will automatically start and manage
// the Kafka Streams topology defined in your @Configuration classes.

package com.aaronag.kafka.config;

import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration  // Marks this class as a Spring configuration provider
public class KafkaStreamsConfig {

    // =====================================================
    // Bean: KafkaStreamsConfiguration
    // =====================================================
    // The @Bean provides the Kafka Streams configuration object.
    //
    // The bean name is REQUIRED to be exactly:
    // "KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME"
    //
    // because Spring Kafka internally looks for this specific bean name
    // to bootstrap the Kafka Streams runtime.
    // =====================================================
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(
            // Inject Kafka bootstrap server address from application.yml
            @Value("${spring.kafka.bootstrap-servers}") String bootstrap,

            // Inject Kafka Streams application ID (must be unique per app)
            @Value("${spring.kafka.streams.application-id}") String appId) {

        // Create a property map for Kafka Streams settings
        Map<String, Object> props = new HashMap<>();

        // =====================================================
        // 1. Kafka broker connection
        // =====================================================
        // Points the Streams API to the Kafka cluster
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

        // =====================================================
        // 2. Application ID
        // =====================================================
        // This ID uniquely identifies your stream processing app.
        // Kafka uses it to store offsets, state stores, and metadata.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);

        // =====================================================
        // 3. Processing Guarantee
        // =====================================================
        // Enables *exactly-once* processing semantics.
        // Ensures that records are not re-processed or lost during retries.
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        // =====================================================
        // Return a KafkaStreamsConfiguration object built from the map.
        // Spring will automatically use it to initialize Kafka Streams.
        // =====================================================
        return new KafkaStreamsConfiguration(props);
    }
}
