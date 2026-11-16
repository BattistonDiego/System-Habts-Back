package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habitoHistory.dtos.DtoAtividade;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoricoRepository extends JpaRepository<Historico,Long> {

    boolean existsByHabito_IdAndData(Long habitoId, LocalDate date);
    Optional<Historico> findByHabito_IdAndData(Long habitoId, LocalDate date);

    List<Historico> findByData(LocalDate data);

    @Query("SELECT h FROM Historico h WHERE h.data = :data")
    List<Historico> searchByData(@Param("data") LocalDate data);

    @Query("SELECT new com.habts.routine.habitoHistory.HistoricoDataResponse(h.id, h.habito.id, h.data, h.concluido) FROM Historico h WHERE h.data = :data")
    List<HistoricoDataResponse> findResponseByData(@Param("data") LocalDate data);

    @Query("SELECT new com.habts.routine.habitoHistory.HistoricoDataResponse(h.id, h.habito.id, h.data, h.concluido) FROM Historico h")
    List<HistoricoDataResponse> findAllResponses();

    @Modifying
    @Transactional
    void deleteByHabito_Id(Long habitoId);

    List<Historico> findByHabito_IdAndHabito_Usuario_EmailOrderByDataDesc(
            Long habitoId,
            String email
    );


    @Query(value = "SELECT TO_CHAR(data, 'Day') as dia, COUNT(*) as quantidade " +
            "FROM tb_historico " +
            "WHERE habito_id IN (SELECT id FROM tb_habito WHERE user_id = :usuarioId) " +
            "AND data BETWEEN :inicio AND :fim " +
            "AND concluido = true " +
            "GROUP BY TO_CHAR(data, 'Day')", nativeQuery = true)
    List<ResumoDiarioDTO> findResumoSemanal(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    @Query("SELECT DISTINCT h.data FROM Historico h " +
            "WHERE h.habito.id = :habitoId " +
            "AND h.habito.usuario.id = :usuarioId " +
            "AND h.concluido = true " +
            "ORDER BY h.data ASC")
    List<LocalDate> findDatasByHabitoEUsuario(@Param("habitoId") Long habitoId,
                                              @Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT COUNT(*) FROM tb_historico " +
            "WHERE habito_id IN (SELECT id FROM tb_habito WHERE user_id = :usuarioId) " +
            "AND EXTRACT(MONTH FROM data) = :mes " +
            "AND EXTRACT(YEAR FROM data) = :ano " +
            "AND concluido = true", nativeQuery = true)
    Long countByMes(@Param("usuarioId") Long usuarioId,
                    @Param("mes") int mes,
                    @Param("ano") int ano);


    List<Historico> findByHabitoOrderByDataDesc(Habito habito);
    List<Historico> findByHabitoAndConcluidoTrueOrderByDataDesc(Habito habito);

    @Query("SELECT new com.habts.routine.habitoHistory.dtos.DtoAtividade(h.data, COUNT(h)) " +
            "FROM Historico h " +
            "WHERE h.habito.usuario.id = :usuarioId " +
            "AND h.data >= :dataInicio " +
            "AND h.concluido = true " +
            "GROUP BY h.data " +
            "ORDER BY h.data DESC")
    List<DtoAtividade> buscarAtividade(@Param("usuarioId") Long usuarioId,
                                       @Param("dataInicio") LocalDate dataInicio);


}
