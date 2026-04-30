package com.habts.routine.habitoHistory.dtos;


public record DetalhesSave(
        Long habitoId,
        boolean status,
        Integer valorAtual
) {
}
