package com.habts.routine.habito;

import com.habts.routine.users.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_habito")
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;
    private String nome;
    private String icon;
    private String color;
    private LocalDate dataCriacao;

    public Habito() {}
//Teste CI
    public Habito(Usuario usuario, String nome, String icon, String color) {
        this.usuario = usuario;
        this.nome = nome;
        this.icon = icon;
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    @PrePersist
    void definirDataCriacao() {
        if (dataCriacao == null) dataCriacao = LocalDate.now();
    }
}
