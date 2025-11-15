package com.aaronag.kafka.service;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StreamsTopology {

  @Value("${app.topics.orders}")
  private String orders;

  @Value("${app.topics.orders_enriched}")
  private String ordersEnriched;

  @Bean
  public KStream<String, String> build(StreamsBuilder builder) {
    KStream<String, String> source = builder.stream(orders);
    KStream<String, String> enriched = source.mapValues(v -> v + " | enriched");
    enriched.to(ordersEnriched, Produced.with(Serdes.String(), Serdes.String()));
    return enriched;
  }
}
