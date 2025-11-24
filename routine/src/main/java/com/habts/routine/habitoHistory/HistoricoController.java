package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/historico")
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
        Historico newHistorico = new Historico(null,
                habito.get(),
                LocalDate.now(),
                dto.status(),
                dto.valorAtual(),
                habito.get().getMeta());

        return ResponseEntity.ok(historicoRepository.save(newHistorico));
    }
}
