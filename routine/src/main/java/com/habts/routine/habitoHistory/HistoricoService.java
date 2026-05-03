package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import com.habts.routine.habitoHistory.dtos.DetalhesSave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private HabitoRepository habitoRepository;

    public Optional<Historico> save(DetalhesSave dto) {
        Optional<Habito> habito = habitoRepository.findById(dto.habitoId());
        if (habito.isEmpty()) return Optional.empty();

        if (historicoRepository.existsByhabitoIdAndData(habito.get().getId(), LocalDate.now())) {
            return Optional.empty();
        }

        Historico newHistorico = new Historico(null,
                habito.get(),
                LocalDate.now(),
                dto.status(),
                dto.valorAtual(),
                habito.get().getMeta());

        return Optional.of(historicoRepository.save(newHistorico));
    }

    public List<Historico> listAll() {
        return historicoRepository.findAll();
    }

    public List<Historico> listByData(LocalDate date) {
        if (date == null) return historicoRepository.findAll();
        return historicoRepository.searchByData(date);
    }

    public int calcularStreak(Long habitoId, String email) {
        List<Historico> registros = historicoRepository
                .findByHabitoIdAndHabitoUsuarioEmailOrderByDataDesc(habitoId, email);

        if (registros.isEmpty()) return 0;

        int streak = 0;
        LocalDate base = LocalDate.now();

        if (!registros.get(0).getData().equals(base)) {
            base = base.minusDays(1);
        }

        for (Historico h : registros) {
            LocalDate esperado = base.minusDays(streak);
            if (h.getData().equals(esperado)) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    public List<ResumoDiarioDTO> getResumoSemanal(Long usuarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana = hoje.with(DayOfWeek.SUNDAY);
        return historicoRepository.findResumoSemanal(usuarioId, inicioSemana, fimSemana);
    }

    public int getMelhorSequencia(Long usuarioId) {
        List<LocalDate> datas = historicoRepository.findDatasByUsuario(usuarioId);

        if (datas.isEmpty()) return 0;

        int melhorSeq = 1;
        int sequenciaAtual = 1;

        for (int i = 1; i < datas.size(); i++) {
            LocalDate diaAnterior = datas.get(i - 1);
            LocalDate diaAtual = datas.get(i);

            if (diaAnterior.plusDays(1).equals(diaAtual)) {
                sequenciaAtual++;
                melhorSeq = Math.max(melhorSeq, sequenciaAtual);
            } else {
                sequenciaAtual = 1;
            }
        }
        return melhorSeq;
    }
}