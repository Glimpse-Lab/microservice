package com.glimpse.notification.listener;

import com.glimpse.notification.config.RabbitMQConfig;
import com.glimpse.notification.dto.OrcamentoEnviadoMessage;
import com.glimpse.notification.service.EmailService;
import com.rabbitmq.client.Channel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrcamentoEnviadoListener {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoEnviadoListener.class);

    private final EmailService emailService;
    private final String adminEmail;

    public OrcamentoEnviadoListener(EmailService emailService,
                                    @Value("${app.admin.email}") String adminEmail) {
        this.emailService = emailService;
        this.adminEmail = adminEmail;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORCAMENTO_ENVIADO)
    public void receberOrcamentoEnviado(
            @Valid @Payload OrcamentoEnviadoMessage message,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Channel channel) throws IOException {
        log.info("Evento de orçamento recebido: orcamentoId={}, cliente={}", message.id(), message.clienteNome());

        emailService.enviarEmailNovoOrcamento(adminEmail, message);
        channel.basicAck(deliveryTag, false);

        log.info("Evento confirmado com sucesso: orcamentoId={}", message.id());
    }
}
