package com.example.demo.rabbitmq;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@EnableConfigurationProperties(RabbitMQShovelProperties.class)
@Configuration
class RabbitMQConfiguration {

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("demo");
    }

    @Bean
    FanoutExchange shovelExchange() {
        return new FanoutExchange("demo.exchange1");
    }

    @Bean
    Queue queue() {
        return new Queue("demo.queue1");
    }

    @Bean
    Binding binding(Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    Client client(final RabbitMQShovelProperties rabbitMqProperties) throws MalformedURLException, URISyntaxException {
        var clientParameters = new ClientParameters()
                .url(rabbitMqProperties.clientUrl())
                .username(rabbitMqProperties.clientUsername())
                .password(rabbitMqProperties.clientPassword());

        return new Client(clientParameters);
    }
}
