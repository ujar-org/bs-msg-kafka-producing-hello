package org.ujar.basics.kafka.producing.hello.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.ujar.basics.kafka.producing.hello.model.Greeting;

@Configuration
@RequiredArgsConstructor
public class KafkaTestConfiguration {
  @Bean
  public ConsumerFactory<String, Greeting> consumeGreetingConsumerFactory(KafkaProperties kafkaProperties) {
    var consumerProperties = kafkaProperties.getConsumer().buildProperties();
    try (var serde = new JsonSerde<>(Greeting.class, new ObjectMapper())) {
      return new DefaultKafkaConsumerFactory<>(consumerProperties,
          new ErrorHandlingDeserializer<>(new StringDeserializer()), new ErrorHandlingDeserializer<>(
          serde.deserializer()));
    }
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Greeting> consumeGreetingKafkaListenerContainerFactory(
      ConsumerFactory<String, Greeting> consumeGreetingConsumerFactory,
      @Value("${ujar.kafka.consumer.threads:2}") int threads) {
    ConcurrentKafkaListenerContainerFactory<String, Greeting> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumeGreetingConsumerFactory);
    factory.setConcurrency(threads);
    return factory;
  }
}
