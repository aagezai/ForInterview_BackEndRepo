package com.aaronag.kafka.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.topics.orders}")
  private String ordersTopic;

  public void send(String key, String value) {
    kafkaTemplate.send(new ProducerRecord<>(ordersTopic, key, value));
  }

  public void sendTransactional(String key, String value) {
    kafkaTemplate.executeInTransaction(ops -> {
      ops.send(ordersTopic, key, value);
      return true;
    });
  }
}
