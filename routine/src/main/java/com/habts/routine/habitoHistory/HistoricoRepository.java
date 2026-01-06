package com.habts.routine.habitoHistory;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HistoricoRepository extends JpaRepository<Historico,Long> {

    boolean existsByhabitoIdAndData(Long habitoid, LocalDate date);

    List<Historico> findByData(LocalDate data);

    @Query("SELECT h FROM Historico h WHERE h.data = :data")
    List<Historico> searchByData(@Param("data") LocalDate data);

    @Modifying
    @Transactional
    void deleteByHabitoId(Long habitoid);
}
