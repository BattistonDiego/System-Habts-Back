package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import com.habts.routine.habitoHistory.dtos.DetalhesSave;
import com.habts.routine.habitoHistory.dtos.DtoEstatisticaHabito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public int getMelhorSequenciaPorHabito(Long usuarioId, Long habitoId) {
        List<LocalDate> datas = historicoRepository
                .findDatasByHabitoEUsuario(habitoId, usuarioId);

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

    public Long getEsteMes(Long usuarioId) {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        return historicoRepository.countByMes(usuarioId, mes, ano);
    }

    public Double getTaxaMedia(Long usuarioId) {
        int mes = LocalDate.now().getMonthValue();
        int ano = LocalDate.now().getYear();
        Double taxa = historicoRepository.getTaxaMedia(usuarioId, mes, ano);
        return taxa != null ? taxa : 0.0;
    }

    public int calcularSequenciaAtual(List<Historico> list) {
        LocalDate dataAnterior = null;
        int contador = 0;

        if (list.isEmpty()) {
            return 0;
        }

        if(ChronoUnit.DAYS.between(list.get(0).getData(), LocalDate.now()) > 1){
            return 0;
        }else{
            for (Historico h : list) {
                if (dataAnterior == null) {
                    dataAnterior = h.getData();
                    contador++;
                } else {
                    if(ChronoUnit.DAYS.between(h.getData(), dataAnterior) == 1){
                        dataAnterior = dataAnterior.minusDays(1);
                        contador++;
                    }else{
                        break;
                    }
                }
            }
        }

        return contador;
    }

    public int calcularMelhorSequencia(List<Historico> list) {


        int contador = 0;
        int maiorSequencia = 0;

        LocalDate dataAnterior = null;

        if (list.isEmpty()) {
            return 0;
        }

        for (Historico h : list) {
            if (dataAnterior == null) {
                dataAnterior = h.getData();
                contador++;
            }else{
                if (ChronoUnit.DAYS.between(h.getData(), dataAnterior) == 1){
                    dataAnterior = dataAnterior.minusDays(1);
                    contador++;
                }else{
                    maiorSequencia = contador;
                    dataAnterior = h.getData();
                    contador = 1;
                }
            }
            if(contador > maiorSequencia){
                maiorSequencia = contador;
            }
        }

        return maiorSequencia;
    }

    public int calcularTaxaConclusao(List<Historico> list) {

        if (list.isEmpty()) {
            return 0;
        }

        int totalRegistros = list.size();
        Historico maisAntigo = list.get(list.size() - 1);
        Long totalDias = ChronoUnit.DAYS.between(maisAntigo.getData(), LocalDate.now()) + 1;


        return totalRegistros * 100 / totalDias.intValue();
    }

    public LocalDate getDataAntiga(List<Historico> list) {
        if (list.isEmpty()) {
            return null;
        }

        Historico maisAntigo = list.get(list.size() - 1);

        return maisAntigo.getData();
    }

    public DtoEstatisticaHabito calcularEstatisticas(Long habitoId) {

        Habito habito = habitoRepository.findById(habitoId).orElseThrow(() -> new RuntimeException("Hábito não encontrado"));
        List<Historico> list  = historicoRepository.findByHabitoOrderByDataDesc(habito);

        int melhorSequencia = calcularMelhorSequencia(list);
        int sequenciaAtual = calcularSequenciaAtual(list);
        int taxaConclusao = calcularTaxaConclusao(list);
        LocalDate registroAntigo = getDataAntiga(list);


        return new DtoEstatisticaHabito(sequenciaAtual,melhorSequencia,taxaConclusao, registroAntigo);

    }

}