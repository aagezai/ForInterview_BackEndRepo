// =============================================
// OrderListener.java
// =============================================
// This class defines a Kafka consumer that listens to the "orders" topic
// and processes incoming messages.
//
// Key concepts:
//  - Uses @KafkaListener to subscribe to a topic
//  - Processes each record (key, value, partition, offset)
//  - Uses manual acknowledgment (configured in ConsumerFactory)
//  - Logs message details and commits offset manually
//
// The listener method executes every time a new message arrives
// in the Kafka topic it’s subscribed to.

package com.aaronag.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j  // Lombok annotation — provides a logger instance named "log"
@Component  // Marks this class as a Spring-managed component (auto-detected)
public class OrderListener {

    // =====================================================
    // @KafkaListener annotation
    // =====================================================
    // - topics: subscribes to the topic name injected from application.yml
    //   (app.topics.orders = "orders")
    // - containerFactory: specifies which listener container bean to use
    //   ("kafkaListenerContainerFactory" — defined in KafkaConsumerFactoryConfig)
    //
    // Each message received will trigger this method call.
    // =====================================================
    @KafkaListener(
            topics = "${app.topics.orders}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {

        // =====================================================
        // Logging the consumed record
        // =====================================================
        // Log message key, value, partition, and offset for observability.
        // This helps in debugging and understanding message flow.
        log.info("✅ Consumed message -> key={}, value={}, partition={}, offset={}",
                record.key(),
                record.value(),
                record.partition(),
                record.offset()
        );

        // =====================================================
        // Manual acknowledgment
        // =====================================================
        // This confirms successful processing to Kafka,
        // advancing the consumer offset so the same message is not reprocessed.
        // You must call this manually since AckMode.MANUAL is set.
        ack.acknowledge();

        // ✅ Once acknowledged, Kafka commits the offset for this record.
    }
}
