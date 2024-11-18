package com.example.demo.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("rabbitmq.shovel.client")
record RabbitMQShovelProperties(
        @DefaultValue("amqp://localhost")
        String sourceUri,
        @DefaultValue("amqp://localhost")
        String destinationUri,
        @DefaultValue("shovel1")
        String shovelName,
        @DefaultValue("demo.queue1")
        String sourceQueue,
        @DefaultValue("demo.exchange1")
        String destinationExchange,
        @DefaultValue("http://localhost:15672/api")
        String clientUrl,
        @DefaultValue("guest")
        String clientUsername,
        @DefaultValue("guest")
        String clientPassword
) {
}
