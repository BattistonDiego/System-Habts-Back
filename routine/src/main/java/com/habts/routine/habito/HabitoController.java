package com.habts.routine.habito;

import com.habts.routine.habito.dtos.DetalhesCadastro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habitos")
public class HabitoController {

    @Autowired
    private HabitoRepository repository;

    @PostMapping
    public ResponseEntity cadastrarHabito(@RequestBody DetalhesCadastro dto){
        var newHabito = new Habito(dto.nome(), dto.meta(), dto.unidade(), dto.icone(), dto.cor());

        var habtitsaved = repository.save(newHabito);
        return ResponseEntity.ok(habtitsaved);
    }

    @GetMapping
    public ResponseEntity<List<Habito>> listHabito(){
        var listHabitos = repository.findAll();
        return ResponseEntity.ok(listHabitos);
    }
}
