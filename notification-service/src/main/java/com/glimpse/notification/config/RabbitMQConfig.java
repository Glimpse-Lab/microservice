package com.glimpse.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NOTIFICACOES = "gpm.notificacoes.exchange";
    public static final String QUEUE_ORCAMENTO_ENVIADO = "gpm.orcamento.enviado.queue";
    public static final String ROUTING_KEY_ORCAMENTO_ENVIADO = "orcamento.enviado";
    public static final String DLX_EXCHANGE = EXCHANGE_NOTIFICACOES + ".dlx";
    public static final String DLQ_QUEUE = QUEUE_ORCAMENTO_ENVIADO + ".dlq";
    public static final String DLQ_ROUTING_KEY = ROUTING_KEY_ORCAMENTO_ENVIADO + ".dlq";

    @Bean
    public TopicExchange notificacoesExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NOTIFICACOES).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(DLX_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue orcamentoEnviadoQueue() {
        return QueueBuilder.durable(QUEUE_ORCAMENTO_ENVIADO)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding orcamentoEnviadoBinding(Queue orcamentoEnviadoQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(orcamentoEnviadoQueue)
                .to(notificacoesExchange)
                .with(ROUTING_KEY_ORCAMENTO_ENVIADO);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
