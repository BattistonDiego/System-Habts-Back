package com.habts.routine.habitoHistory;

import java.time.LocalDate;

public record DetalhesSave(
        Long habitoId,
        boolean status,
        Integer valorAtual
) {
}
