package com.hso.sagaordermanager.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConf {
    @Bean
    public Queue queueOS() {
        return new Queue("order.service");
    }

    @Bean
    public Queue queueIS() {
        return new Queue("inventory.service");
    }
    @Bean
    public Queue queueIR() {
        return new Queue("inventory.response");
    }
    @Bean
    public Queue queuePS() {
        return new Queue("payment.service");
    }
    @Bean
    public Queue queuePR() {
        return new Queue("payment.response");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("saga.poc");
    }

    @Bean
    public Binding bindingOs(Queue queueOS, DirectExchange exchange) {
        return BindingBuilder.bind(queueOS).to(exchange).with("order.key");
    }
    @Bean
    public Binding bindingIs(Queue queueIS, DirectExchange exchange) {
        return BindingBuilder.bind(queueIS).to(exchange).with("inventory.key");
    }    @Bean
    public Binding bindingIr(Queue queueIR, DirectExchange exchange) {
        return BindingBuilder.bind(queueIR).to(exchange).with("inventory.response.key");
    }    @Bean
    public Binding bindingPs(Queue queuePS, DirectExchange exchange) {
        return BindingBuilder.bind(queuePS).to(exchange).with("payment.key");
    }    @Bean
    public Binding bindingPr(Queue queuePR, DirectExchange exchange) {
        return BindingBuilder.bind(queuePR).to(exchange).with("payment.response.key");
    }
}
