package com.glimpse.notification.infrastructure.messaging;

import com.glimpse.notification.domain.port.in.ProcessarOrcamentoUseCase;
import com.glimpse.notification.infrastructure.config.RabbitMQConfig;
import com.glimpse.notification.infrastructure.mapper.OrcamentoNotificacaoMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoEnviadoListener {

    private static final Logger log = LoggerFactory.getLogger(OrcamentoEnviadoListener.class);

    private final ProcessarOrcamentoUseCase useCase;
    private final OrcamentoNotificacaoMapper mapper;

    public OrcamentoEnviadoListener(ProcessarOrcamentoUseCase useCase,
                                    OrcamentoNotificacaoMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ORCAMENTO_ENVIADO)
    public void receberOrcamentoEnviado(@Valid @Payload OrcamentoEnviadoMessage message) {
        log.info("Mensagem recebida: orcamentoId={}", message.id());
        useCase.processar(mapper.toDomain(message));
    }
}