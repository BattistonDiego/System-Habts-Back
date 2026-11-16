package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@Entity
@Table(name = "tb_historico")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "habito_id")
    private Habito  habito;
    private LocalDate data;
    private boolean concluido;

    public Historico(Long id, Habito habito, LocalDate data, boolean concluido) {
        this.id = id;
        this.habito = habito;
        this.data = data;
        this.concluido = concluido;
    }

    public Historico() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Habito getHabito() {
        return habito;
    }

    public void setHabito(Habito habito) {
        this.habito = habito;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Long getHabitoId() {
        return habito == null ? null : habito.getId();
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) { this.concluido = concluido; }


}
