package com.habts.routine.habito;

import com.habts.routine.habito.dtos.DetalhesCadastro;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_habito")
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer meta;
    private String unidade;
    private String icone;
    private String cor;

    public Habito() {}

    Habito(String nome, Integer meta, String unidade, String icone, String cor){
        this.nome = nome;
        this.meta = meta;
        this.unidade = unidade;
        this.icone = icone;
        this.cor = cor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
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

    public Integer getMeta() {
        return meta;
    }

    public void setMeta(Integer meta) {
        this.meta = meta;
    }


}
