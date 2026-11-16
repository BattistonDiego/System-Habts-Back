package com.habts.routine.habitoHistory;

import com.habts.routine.Kafka.HistoricoRegistradoEvento;
import com.habts.routine.exception.RecursoExistenteException;
import com.habts.routine.exception.RecursoNaoEncontradoException;
import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import com.habts.routine.habitoHistory.dtos.DetalhesSave;
import com.habts.routine.habitoHistory.dtos.DtoAtividade;
import com.habts.routine.habitoHistory.dtos.DtoEstatisticaHabito;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import com.habts.routine.Kafka.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private static final Logger log = LoggerFactory.getLogger(HistoricoService.class);

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private HabitoRepository habitoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private EventPublisher eventPublisher;

    @Transactional
    public Historico save(DetalhesSave dto) {
        Habito habito = habitoRepository.findById(dto.habitoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Hábito não encontrado"));

        Optional<Historico> registroExistente = historicoRepository.findByHabito_IdAndData(habito.getId(), LocalDate.now());

        Historico newHistorico;
        if (registroExistente.isPresent()) {
            newHistorico = registroExistente.get();
            if (!dto.concluido() || newHistorico.isConcluido()) {
                throw new RecursoExistenteException("Historico existente");
            }
            newHistorico.setConcluido(true);
        } else {
            newHistorico = new Historico(null, habito, LocalDate.now(), dto.concluido());
        }

        Historico historicoSalvo = historicoRepository.save(newHistorico);
        log.info("Histórico salvo com sucesso");

        Long usuarioId = habito.getUsuario().getId();

        HistoricoRegistradoEvento evento = new HistoricoRegistradoEvento(
                historicoSalvo.getId(),
                habito.getId(),
                usuarioId,
                historicoSalvo.getData(),
                historicoSalvo.isConcluido()
        );

        if (eventPublisher != null) {
            Runnable publish = () -> eventPublisher.publish(evento);
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        publish.run();
                    }
                });
            } else {
                publish.run();
            }
        } else {
            log.warn("Kafka indisponível, evento não enviado");
        }

        return historicoSalvo;
    }

    public List<Historico> listAll() {
        return historicoRepository.findAll();
    }

    public List<HistoricoDataResponse> listByData(LocalDate date) {
        if (date == null) return historicoRepository.findAllResponses();
        return historicoRepository.findResponseByData(date);
    }

    public int calcularStreak(Long habitoId, String email) {
        List<Historico> registros = historicoRepository
                .findByHabito_IdAndHabito_Usuario_EmailOrderByDataDesc(habitoId, email)
                .stream().filter(Historico::isConcluido).toList();

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

    public LocalDate getDataAntiga(List<Historico> list) {
        if (list.isEmpty()) {
            return null;
        }

        Historico maisAntigo = list.get(list.size() - 1);

        return maisAntigo.getData();
    }

    public DtoEstatisticaHabito calcularEstatisticas(Long habitoId) {

        Habito habito = habitoRepository.findById(habitoId).orElseThrow(() -> new RecursoNaoEncontradoException("Hábito não encontrado"));
        List<Historico> list  = historicoRepository.findByHabitoAndConcluidoTrueOrderByDataDesc(habito);

        int melhorSequencia = calcularMelhorSequencia(list);
        int sequenciaAtual = calcularSequenciaAtual(list);
        LocalDate registroAntigo = getDataAntiga(list);


        return new DtoEstatisticaHabito(sequenciaAtual,melhorSequencia, registroAntigo, list.size());

    }


    public List<DtoAtividade> buscarAtividades(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var userLogged = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(userLogged)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        LocalDate dataInicio = LocalDate.now().minusMonths(6);

        return historicoRepository.buscarAtividade(user.getId(),dataInicio);
    }






}
