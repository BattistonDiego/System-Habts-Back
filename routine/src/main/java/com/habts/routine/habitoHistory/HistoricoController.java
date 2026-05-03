package com.habts.routine.habitoHistory;
import com.habts.routine.habitoHistory.Historico;
import com.habts.routine.habitoHistory.HistoricoService;
import com.habts.routine.habitoHistory.ResumoDiarioDTO;
import com.habts.routine.habitoHistory.dtos.DetalhesSave;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/historico")
@SecurityRequirement(name = "bearer-key")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;


    private SecurityContextHolder securityContextHolder;

    @PostMapping
    public ResponseEntity<Historico> save(@RequestBody DetalhesSave dto) {
        return historicoService.save(dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<Historico>> listAll() {
        return ResponseEntity.ok(historicoService.listAll());
    }

    @GetMapping("/data")
    public ResponseEntity<List<Historico>> listByData(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(historicoService.listByData(date));
    }

    @GetMapping("/streak/{habitoId}")
    public int calcularStreak(@PathVariable Long habitoId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return historicoService.calcularStreak(habitoId, email);
    }

    @GetMapping("/resumo-semanal/{usuarioId}")
    public List<ResumoDiarioDTO> getResumoSemanal(@PathVariable Long usuarioId) {
        return historicoService.getResumoSemanal(usuarioId);
    }

    @GetMapping("/melhor-sequencia/{usuarioId}")
    public int getMelhorSequencia(@PathVariable Long usuarioId) {
        return historicoService.getMelhorSequencia(usuarioId);
    }
}