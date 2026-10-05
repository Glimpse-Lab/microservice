package com.glimpse.notification.service;

import com.glimpse.notification.dto.OrcamentoEnviadoMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy 'às' HH:mm")
            .withLocale(new Locale("pt", "BR"));

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;
    private final String baseUrl;

    public EmailService(JavaMailSender mailSender,
                        TemplateEngine templateEngine,
                        @Value("${app.mail.from}") String fromEmail,
                        @Value("${app.base-url}") String baseUrl) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.baseUrl = baseUrl;
    }

    @Async("emailTaskExecutor")
    public void enviarEmailRecuperacao(String emailDestino, String linkRecuperacao) {
        Context context = new Context();
        context.setVariable("link", linkRecuperacao);
        enviar(emailDestino, "Redefinição de Senha - GPM", "email-recuperacao", context);
    }

    public void enviarEmailNovoOrcamento(String emailDestino, OrcamentoEnviadoMessage event) {
        log.info("Enviando e-mail de novo orçamento para {} (orcamentoId={})", emailDestino, event.id());

        Context context = new Context();
        context.setVariable("orcamentoId", event.id());
        context.setVariable("clienteNome", event.clienteNome());
        context.setVariable("clienteEmail", event.clienteEmail());
        context.setVariable("descricaoProjeto", event.descricaoProjeto());
        context.setVariable("endereco", event.endereco());
        context.setVariable("status", event.status());
        context.setVariable("dataSolicitacao", event.dataSolicitacao() == null
                ? ""
                : event.dataSolicitacao().format(DATE_FORMATTER));
        context.setVariable("baseUrl", baseUrl);

        enviar(emailDestino, "Novo orçamento recebido - GPM", "email-novo-orcamento", context);
        log.info("E-mail de novo orçamento enviado (orcamentoId={})", event.id());
    }

    private void enviar(String emailDestino, String subject, String template, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailDestino);
            helper.setSubject(subject);
            helper.setText(templateEngine.process(template, context), true);
            helper.setFrom(fromEmail);
            mailSender.send(message);
        } catch (MessagingException exception) {
            throw new IllegalStateException("Falha ao enviar e-mail para " + emailDestino, exception);
        }
    }
}
