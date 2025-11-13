package com.aaronag.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TopicConfig {

  @Value("${app.topics.orders}")
  private String orders;

  @Value("${app.topics.orders_dlq}")
  private String ordersDlq;

  @Value("${app.topics.orders_enriched}")
  private String ordersEnriched;

  @Bean
  NewTopic ordersTopic() {
    return new NewTopic(orders, 3, (short) 1)
        .configs(Map.of(
            "cleanup.policy", "delete",
            "retention.ms", "604800000"
        ));
  }

  @Bean
  NewTopic ordersDlqTopic() {
    return new NewTopic(ordersDlq, 3, (short) 1)
        .configs(Map.of(
            "cleanup.policy", "delete",
            "retention.ms", "1209600000"
        ));
  }

  @Bean
  NewTopic ordersEnrichedTopic() {
    return new NewTopic(ordersEnriched, 3, (short) 1)
        .configs(Map.of(
            "cleanup.policy", "compact,delete",
            "min.cleanable.dirty.ratio", "0.5"
        ));
  }
}
