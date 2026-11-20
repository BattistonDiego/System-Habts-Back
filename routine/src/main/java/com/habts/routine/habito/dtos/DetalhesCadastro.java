package com.habts.routine.habito.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DetalhesCadastro(

        @NotNull
        @Valid
        Long usuarioId,
        String nome,
        String icon,
        String color,
        String descricao) {
}
