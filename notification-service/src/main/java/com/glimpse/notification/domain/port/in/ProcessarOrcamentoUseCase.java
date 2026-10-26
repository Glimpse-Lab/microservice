package com.glimpse.notification.domain.port.in;

import com.glimpse.notification.domain.model.OrcamentoNotificacao;

public interface ProcessarOrcamentoUseCase {
    void processar(OrcamentoNotificacao notificacao);
}