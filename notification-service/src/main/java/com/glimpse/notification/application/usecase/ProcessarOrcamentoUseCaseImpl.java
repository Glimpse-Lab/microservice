package com.glimpse.notification.application.usecase;

import com.glimpse.notification.domain.model.OrcamentoNotificacao;
import com.glimpse.notification.domain.port.in.ProcessarOrcamentoUseCase;
import com.glimpse.notification.domain.port.out.EmailNotificacaoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProcessarOrcamentoUseCaseImpl implements ProcessarOrcamentoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessarOrcamentoUseCaseImpl.class);

    private final EmailNotificacaoPort emailPort;
    private final String adminEmail;

    public ProcessarOrcamentoUseCaseImpl(EmailNotificacaoPort emailPort,
                                         @Value("${app.admin.email}") String adminEmail) {
        this.emailPort = emailPort;
        this.adminEmail = adminEmail;
    }

    @Override
    public void processar(OrcamentoNotificacao notificacao) {
        log.info("Processando notificação: orcamentoId={}, cliente={}", notificacao.id(), notificacao.clienteNome());
        emailPort.enviarNovoOrcamento(adminEmail, notificacao);
        log.info("Notificação processada: orcamentoId={}", notificacao.id());
    }
}