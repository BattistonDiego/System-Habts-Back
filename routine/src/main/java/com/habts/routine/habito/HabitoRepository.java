package com.habts.routine.habito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoRepository extends JpaRepository<Habito, Long> {

    /** Hábitos do usuário em ordem estável (id crescente = ordem de criação). */
    List<Habito> findByUsuarioIdOrderByIdAsc(Long usuarioId);

}
