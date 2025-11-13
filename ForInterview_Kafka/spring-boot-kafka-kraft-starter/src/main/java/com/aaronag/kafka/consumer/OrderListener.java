package com.aaronag.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderListener {

  @KafkaListener(topics = "${app.topics.orders}", containerFactory = "kafkaListenerContainerFactory")
  public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
    try {
      log.info("Consumed key={}, value={}, partition={}, offset={}",
          record.key(), record.value(), record.partition(), record.offset());
      // business logic here
      ack.acknowledge();
    } catch (Exception ex) {
      log.error("Processing failed; will be retried or sent to DLQ", ex);
      throw ex; // handled by DefaultErrorHandler
    }
  }
}
