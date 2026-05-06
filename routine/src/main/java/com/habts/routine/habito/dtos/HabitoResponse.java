package com.habts.routine.habito.dtos;

public record HabitoResponse(Long id,
                             String nome,
                             Integer meta,
                             String unidade,
                             String icone,
                             String cor,
                             Integer ordem) {
}
