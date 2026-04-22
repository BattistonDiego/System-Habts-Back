package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/historico")
@SecurityRequirement(name = "bearer-key")
public class HistoricoController {

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private HabitoRepository habitoRepository;


    @PostMapping
    public ResponseEntity<Historico> save(@RequestBody DetalhesSave dto){
        Optional<Habito> habito = habitoRepository.findById(dto.habitoId());
        if(habito.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if (historicoRepository.existsByhabitoIdAndData(habito.get().getId(), LocalDate.now())){
            return ResponseEntity.badRequest().build();
        }
        Historico newHistorico = new Historico(null,
                habito.get(),
                LocalDate.now(),
                dto.status(),
                dto.valorAtual(),
                habito.get().getMeta());

        return ResponseEntity.ok(historicoRepository.save(newHistorico));
    }

    @GetMapping
    public ResponseEntity<List<Historico>> listAllHistorico(){
       return ResponseEntity.ok(historicoRepository.findAll());
    }

    @GetMapping("/data")
    public ResponseEntity<List<Historico>> listHistoricoByData(@RequestParam(required = false) LocalDate date){

        if(date == null){
            return ResponseEntity.ok(historicoRepository.findAll());
        }
        return ResponseEntity.ok(historicoRepository.searchByData(date));
    }

    @GetMapping("/streak/{habitoId}")
    public int calcularStreak(@PathVariable Long habitoId){

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        List<Historico> registros = historicoRepository.findByHabitoIdAndHabitoUsuarioEmailOrderByDataDesc(habitoId, email);

        if(registros.isEmpty()){
            return 0;
        }

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


}
