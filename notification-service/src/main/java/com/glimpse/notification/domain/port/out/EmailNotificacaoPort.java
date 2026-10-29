package com.glimpse.notification.domain.port.out;

import com.glimpse.notification.domain.model.OrcamentoNotificacao;

public interface EmailNotificacaoPort {
    void enviarNovoOrcamento(String destinatario, OrcamentoNotificacao notificacao);
}