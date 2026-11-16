package com.habts.routine.habitoHistory;

import java.time.LocalDate;

public record HistoricoDataResponse(Long id, Long habitoId, LocalDate data, boolean concluido) {
}