package com.habts.routine.habitoHistory;


public record DetalhesSave(
        Long habitoId,
        boolean status,
        Integer valorAtual
) {
}
