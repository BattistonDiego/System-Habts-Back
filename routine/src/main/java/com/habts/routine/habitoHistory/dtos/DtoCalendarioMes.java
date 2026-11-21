package com.habts.routine.habitoHistory.dtos;

import java.util.List;

public record DtoCalendarioMes(int ano, int mes, List<DtoHabitoMes> habitos, List<DtoConclusaoMes> conclusoes) {
}
