package com.glimpse.notification.infrastructure.mapper;

import com.glimpse.notification.domain.model.OrcamentoNotificacao;
import com.glimpse.notification.infrastructure.messaging.OrcamentoEnviadoMessage;
import org.springframework.stereotype.Component;

@Component
public class OrcamentoNotificacaoMapper {

    public OrcamentoNotificacao toDomain(OrcamentoEnviadoMessage message) {
        return new OrcamentoNotificacao(
                message.id(),
                message.clienteNome(),
                message.clienteEmail(),
                message.descricaoProjeto(),
                message.endereco(),
                message.status(),
                message.dataSolicitacao()
        );
    }
}