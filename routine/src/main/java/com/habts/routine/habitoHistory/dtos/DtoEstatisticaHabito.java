package com.habts.routine.habitoHistory.dtos;

import java.time.LocalDate;

public record DtoEstatisticaHabito(int sequenciaAtual, int melhorSequencia, LocalDate inicio, int totalRegistro) {
}
