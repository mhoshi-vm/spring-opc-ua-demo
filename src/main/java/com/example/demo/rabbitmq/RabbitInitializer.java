package com.example.demo.rabbitmq;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ShovelDetails;
import com.rabbitmq.http.client.domain.ShovelInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "config.rabbitmq", havingValue = "true")
class RabbitInitializer {
    Client client;

    RabbitMQShovelProperties rabbitMQShovelProperties;


    public RabbitInitializer(Client client, RabbitMQShovelProperties rabbitMQShovelProperties) {
        this.client = client;
        this.rabbitMQShovelProperties = rabbitMQShovelProperties;
    }

    @PostConstruct
    void createShovel() {
        String sourceUri = this.rabbitMQShovelProperties.sourceUri();
        String destinationUri = this.rabbitMQShovelProperties.destinationUri();
        String shovelName = this.rabbitMQShovelProperties.shovelName();

        var shovelDetails = new ShovelDetails(sourceUri, destinationUri, 60L, false, null);
        shovelDetails.setSourceQueue(this.rabbitMQShovelProperties.sourceQueue());
        shovelDetails.setDestinationExchange(this.rabbitMQShovelProperties.destinationExchange());

        var shovelInfo = new ShovelInfo(shovelName, shovelDetails);
        client.deleteShovel("/", shovelName);
        client.declareShovel("/", shovelInfo);
    }


}
