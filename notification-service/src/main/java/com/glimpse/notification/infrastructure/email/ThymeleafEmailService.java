package com.glimpse.notification.infrastructure.email;

import com.glimpse.notification.domain.model.OrcamentoNotificacao;
import com.glimpse.notification.domain.port.out.EmailNotificacaoPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ThymeleafEmailService implements EmailNotificacaoPort {

    private static final Logger log = LoggerFactory.getLogger(ThymeleafEmailService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy 'às' HH:mm")
            .withLocale(new Locale("pt", "BR"));

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;
    private final String baseUrl;

    public ThymeleafEmailService(JavaMailSender mailSender,
                                 TemplateEngine templateEngine,
                                 @Value("${app.mail.from}") String fromEmail,
                                 @Value("${app.base-url}") String baseUrl) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.baseUrl = baseUrl;
    }

    @Override
    public void enviarNovoOrcamento(String destinatario, OrcamentoNotificacao notificacao) {
        Context ctx = new Context();
        ctx.setVariable("orcamentoId", notificacao.id());
        ctx.setVariable("clienteNome", notificacao.clienteNome());
        ctx.setVariable("clienteEmail", notificacao.clienteEmail());
        ctx.setVariable("descricaoProjeto", notificacao.descricaoProjeto());
        ctx.setVariable("endereco", notificacao.endereco());
        ctx.setVariable("status", notificacao.status());
        ctx.setVariable("dataSolicitacao", notificacao.dataSolicitacao() == null
                ? "" : notificacao.dataSolicitacao().format(FORMATTER));
        ctx.setVariable("baseUrl", baseUrl);

        enviar(destinatario, "Novo orçamento recebido - GPM", "email-novo-orcamento", ctx);
    }

    private void enviar(String destinatario, String assunto, String template, Context ctx) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(templateEngine.process(template, ctx), true);
            helper.setFrom(fromEmail);
            mailSender.send(msg);
            log.info("E-mail enviado: destinatario={}, template={}", destinatario, template);
        } catch (MessagingException e) {
            throw new IllegalStateException("Falha ao enviar e-mail para " + destinatario, e);
        }
    }
}