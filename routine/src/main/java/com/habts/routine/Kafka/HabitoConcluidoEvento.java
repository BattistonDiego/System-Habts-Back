package com.habts.routine.Kafka;

import java.time.LocalDate;

public class HabitoConcluidoEvento {

    private Long habitoId;
    private Long usuarioId;
    private LocalDate dataConclusao;

    public HabitoConcluidoEvento() {
    }

    public HabitoConcluidoEvento(Long habitoId, Long usuarioId, LocalDate dataConclusao) {
        this.habitoId = habitoId;
        this.usuarioId = usuarioId;
        this.dataConclusao = dataConclusao;
    }

    public Long getHabitoId() {
        return habitoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    @Override
    public String toString() {
        return "HabitoConcluidoEvento{" +
                "habitoId=" + habitoId +
                ", usuarioId=" + usuarioId +
                ", dataConclusao=" + dataConclusao +
                '}';
    }
}
