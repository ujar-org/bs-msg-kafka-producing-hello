package org.ujar.basics.kafka.producing.hello.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.ujar.basics.kafka.producing.hello.model.Greeting;

@Configuration
class KafkaConfiguration {
  @Bean
  public ProducerFactory<String, Greeting> reportMessageProducerFactory(KafkaProperties kafkaProperties) {
    try (var serde = new JsonSerde<>(Greeting.class, new ObjectMapper())) {
      var producerProperties = kafkaProperties.getProducer().buildProperties();
      var producerFactory = new DefaultKafkaProducerFactory<>(producerProperties,
          new StringSerializer(),
          serde.serializer());
      producerFactory.setTransactionIdPrefix(getTransactionPrefix());
      return producerFactory;
    }
  }

  @Bean
  public KafkaTemplate<String, Greeting> reportMessageKafkaTemplate(
      ProducerFactory<String, Greeting> reportMessageProducerFactory) {
    return new KafkaTemplate<>(reportMessageProducerFactory);
  }

  private String getTransactionPrefix() {
    return "tx-" + UUID.randomUUID() + "-";
  }
}
