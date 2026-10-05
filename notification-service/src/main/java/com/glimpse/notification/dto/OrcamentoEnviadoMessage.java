package com.glimpse.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrcamentoEnviadoMessage(
        @NotNull Long id,
        @NotBlank String clienteNome,
        @NotBlank @Email String clienteEmail,
        @NotBlank String descricaoProjeto,
        String endereco,
        @NotBlank String status,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataSolicitacao
) {
}
