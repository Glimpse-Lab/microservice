package com.glimpse.notification.domain.model;

import java.time.LocalDateTime;

public record OrcamentoNotificacao(
        Long id,
        String clienteNome,
        String clienteEmail,
        String descricaoProjeto,
        String endereco,
        String status,
        LocalDateTime dataSolicitacao
) {}