package com.habts.routine.habito;

import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.habitoHistory.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/habitos")
public class HabitoController {

    @Autowired
    private HabitoRepository habitoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;


    @PostMapping
    public ResponseEntity cadastrarHabito(@RequestBody DetalhesCadastro dto){
        var newHabito = new Habito(dto.nome(), dto.meta(), dto.unidade(), dto.icone(), dto.cor());

        var habtitsaved = habitoRepository.save(newHabito);
        return ResponseEntity.ok(habtitsaved);
    }

    @GetMapping
    public ResponseEntity<List<Habito>> listHabito(){
        var listHabitos = habitoRepository.findAll();
        return ResponseEntity.ok(listHabitos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteHabito(@PathVariable Long id){
        historicoRepository.deleteByHabitoId(id);
        habitoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habito> editHabito(@PathVariable Long id,  @RequestBody DetalhesCadastro dto){
        Optional<Habito> habito = habitoRepository.findById(id);

        if(habito.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(dto.nome() != null){
            habito.get().setNome(dto.nome());
        }

         if (dto.meta() != null){
             habito.get().setMeta(dto.meta());
         }

        if (dto.unidade() != null){
            habito.get().setUnidade(dto.unidade());
        }
        if (dto.icone() != null){
            habito.get().setIcone(dto.icone());
        }
        if (dto.cor() != null){
            habito.get().setCor(dto.cor());
        }

        var habitEdited = habitoRepository.save(habito.get());

        return ResponseEntity.ok(habitEdited);
    }
}
