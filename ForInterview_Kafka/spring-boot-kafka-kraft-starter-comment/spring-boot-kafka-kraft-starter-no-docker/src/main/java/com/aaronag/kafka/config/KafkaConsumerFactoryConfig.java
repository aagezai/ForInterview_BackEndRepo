// =============================================
// KafkaConsumerFactoryConfig.java
// =============================================
// This configuration class defines how Spring Kafka creates and manages
// consumer containers used by @KafkaListener methods.
// It sets manual acknowledgment mode, error handling with DLQ, and concurrency.
//
// Key points:
//  - Builds a ConcurrentKafkaListenerContainerFactory bean.
//  - Connects to the ConsumerFactory (bootstrap + deserializers).
//  - Uses a DefaultErrorHandler to publish failed records to a DLQ.
//  - Enables manual offset acknowledgment and metrics observation.

package com.aaronag.kafka.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration  // Marks this as a configuration class for Spring Boot
public class KafkaConsumerFactoryConfig {

  // Injects DLQ topic name from application.yml
  // e.g. app.topics.orders_dlq: orders.dlq
  @Value("${app.topics.orders_dlq}")
  String dlqTopic;

  // =====================================================
  // Bean: kafkaListenerContainerFactory
  // =====================================================
  // This bean tells Spring how to create Kafka listener containers
  // used by any @KafkaListener methods in your project.
  //
  // Parameters:
  //  - ConsumerFactory: builds Kafka consumers with bootstrap servers, deserializers, etc.
  //  - KafkaTemplate: used by DeadLetterPublishingRecoverer to forward failed messages.
  //
  // Returns:
  //  - A configured ConcurrentKafkaListenerContainerFactory for String key/value consumers.
  // =====================================================
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
          ConsumerFactory<String, String> cf, KafkaTemplate<String, String> template) {

    // Create the listener container factory instance
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

    // Assign the shared ConsumerFactory
    factory.setConsumerFactory(cf);

    // Enable Micrometer observation (for metrics/tracing if enabled)
    factory.getContainerProperties().setObservationEnabled(true);

    // Allow application to start even if the topic doesn’t exist yet
    factory.getContainerProperties().setMissingTopicsFatal(false);

    // Set manual acknowledgment mode
    // The listener method must explicitly call ack.acknowledge() after processing
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

    // =====================================================
    // Configure Error Handler (DLQ)
    // =====================================================
    // DefaultErrorHandler handles exceptions thrown during message processing.
    // Here we wrap it with a DeadLetterPublishingRecoverer so failed messages
    // are sent to a dedicated DLQ topic instead of being lost.
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(template,
                    // Define how to determine which partition to send failed message to
                    (record, ex) -> new TopicPartition(dlqTopic, record.partition()))
    );

    // Attach our error handler to the factory
    factory.setCommonErrorHandler(errorHandler);

    // Set concurrency to 3
    // Means Spring will create 3 KafkaConsumer threads for parallel processing
    factory.setConcurrency(3);

    // Set poll timeout (how long to block waiting for new messages)
    factory.getContainerProperties().setPollTimeout(1000);

    // Return fully configured factory bean
    return factory;
  }
}
