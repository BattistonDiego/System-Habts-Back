package com.habts.routine.habito.dtos;

import com.habts.routine.habito.Habito;

public record DtoResponse(String nomeUsuario, String nomeHabito, Integer meta, String unidade) {

    public static DtoResponse fromHabito(Habito habito) {
        return new DtoResponse(
                habito.getUsuario().getNome(),
                habito.getNome(),
                habito.getMeta(),
                habito.getUnidade()

        );

    }
}
