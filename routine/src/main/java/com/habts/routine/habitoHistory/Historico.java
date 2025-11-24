package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import jakarta.persistence.*;

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
    private boolean status;
    private Integer valorAtual;
    private Integer meta;

    public Historico(Long id, Habito habito, LocalDate data, boolean status, Integer valorAtual, Integer meta) {
        this.id = id;
        this.habito = habito;
        this.data = data;
        this.status = status;
        this.valorAtual = valorAtual;
        this.meta = meta;
    }

    public Historico() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(Integer valorAtual) {
        this.valorAtual = valorAtual;
    }

    public Integer getMeta() {
        return meta;
    }

    public void setMeta(Integer meta) {
        this.meta = meta;
    }
}
