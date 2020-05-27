package com.example.demo.rabbitmq.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TopicRabbitConfig {

    @Value("${queue.name}")
    private String queueName;
    @Value("${exchange.name}")
    private String exchangeName;
    @Value("${router.key}")
    private String routerKey;

    @Bean
    public Queue firstQueue() {
        return new Queue(queueName);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding bindingExchangeMessage() {
        System.out.println("绑定键：" + routerKey);
        return BindingBuilder.bind(firstQueue()).to(exchange()).with(routerKey);
    }
}
