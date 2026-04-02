package com.habts.routine.habito.dtos;

import com.habts.routine.users.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DetalhesCadastro(

        @NotNull
        @Valid
        Long usuarioId,
        String nome,
        Integer meta,
        String unidade,
        String icone,
        String cor) {
}
