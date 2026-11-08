package com.habts.routine.Kafka;

import java.time.LocalDate;

public class HistoricoRegistradoEvento {
    private Long historicoId;
    private Long habitoId;
    private Long usuarioId;
    private LocalDate data;
    private boolean status;

    public HistoricoRegistradoEvento() {
    }

    public HistoricoRegistradoEvento(Long historicoId, Long habitoId, Long usuarioId, LocalDate data, boolean status) {
        this.historicoId = historicoId;
        this.habitoId = habitoId;
        this.usuarioId = usuarioId;
        this.data = data;
        this.status = status;
    }

    public Long getHistoricoId() {
        return historicoId;
    }

    public Long getHabitoId() {
        return habitoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getData() {
        return data;
    }

    public boolean getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "HistoricoRegistradoEvento{" +
                "historicoId=" + historicoId +
                ", habitoId=" + habitoId +
                ", usuarioId=" + usuarioId +
                ", data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
