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

    List<Historico> findByHabitoIdAndHabitoUsuarioEmailOrderByDataDesc(
            Long habitoId,
            String email
    );


    @Query(value = "SELECT TO_CHAR(data, 'Day') as dia, COUNT(*) as quantidade " +
            "FROM tb_historico " +
            "WHERE habito_id IN (SELECT id FROM tb_habito WHERE user_id = :usuarioId) " +
            "AND data BETWEEN :inicio AND :fim " +
            "GROUP BY TO_CHAR(data, 'Day')", nativeQuery = true)
    List<ResumoDiarioDTO> findResumoSemanal(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query("SELECT DISTINCT h.data FROM Historico h " +
            "WHERE h.habito.id = :habitoId " +
            "AND h.habito.usuario.id = :usuarioId " +
            "ORDER BY h.data ASC")
    List<LocalDate> findDatasByHabitoEUsuario(@Param("habitoId") Long habitoId,
                                              @Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT COUNT(*) FROM tb_historico " +
            "WHERE habito_id IN (SELECT id FROM tb_habito WHERE user_id = :usuarioId) " +
            "AND EXTRACT(MONTH FROM data) = :mes " +
            "AND EXTRACT(YEAR FROM data) = :ano", nativeQuery = true)
    Long countByMes(@Param("usuarioId") Long usuarioId,
                    @Param("mes") int mes,
                    @Param("ano") int ano);


    @Query(value = "SELECT " +
            "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_habito WHERE user_id = :usuarioId) / " +
            "COUNT(DISTINCT data)) " +
            "FROM tb_historico " +
            "WHERE habito_id IN (SELECT id FROM tb_habito WHERE user_id = :usuarioId) " +
            "AND EXTRACT(MONTH FROM data) = :mes " +
            "AND EXTRACT(YEAR FROM data) = :ano", nativeQuery = true)
    Double getTaxaMedia(@Param("usuarioId") Long usuarioId,
                        @Param("mes") int mes,
                        @Param("ano") int ano);


}
