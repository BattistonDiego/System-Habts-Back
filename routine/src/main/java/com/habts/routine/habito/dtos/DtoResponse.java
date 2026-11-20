package com.habts.routine.habito.dtos;

import com.habts.routine.habito.Habito;

public record DtoResponse(String nomeUsuario, String nomeHabito, String icon, String color, String descricao) {

    public static DtoResponse fromHabito(Habito habito) {
        return new DtoResponse(
                habito.getUsuario().getNome(),
                habito.getNome(),
                habito.getIcon(),
                habito.getColor(),
                habito.getDescricao()

        );

    }
}
