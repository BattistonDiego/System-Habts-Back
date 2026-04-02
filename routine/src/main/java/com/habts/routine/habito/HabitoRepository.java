package com.habts.routine.habito;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitoRepository extends JpaRepository<Habito, Long> {

    public List<Habito> findByUsuarioId(Long id);
}
