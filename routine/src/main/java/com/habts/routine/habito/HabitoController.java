package com.habts.routine.habito;

import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.habito.dtos.HabitoResponse;
import com.habts.routine.habitoHistory.HistoricoRepository;
import com.habts.routine.treinos.TesteComponent;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/habitos")
@SecurityRequirement(name = "bearer-key")
public class HabitoController {

    @Autowired
    private HabitoRepository habitoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private HabitoService habitoService;

    @Autowired
    private TesteComponent testeComponent;


    @PostMapping
    public ResponseEntity cadastrarHabito(@RequestBody @Valid DetalhesCadastro dto){
        return habitoService.createHabito(dto);
    }

    @GetMapping
    public ResponseEntity<List<HabitoResponse>> listHabito(){
        return habitoService.listAllHabito();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteHabito(@PathVariable Long id){
        return habitoService.deleteHabito(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habito> editHabito(@PathVariable Long id,  @RequestBody DetalhesCadastro dto){
        return habitoService.editarHabito(id, dto);
    }

    @GetMapping("/testeComponent")
    public Map<String, Long> soma(){
        Long resultado = testeComponent.somar(1L, 1L);

        Map<String, Long> soma = new HashMap<>();

        soma.put("Soma de 1 + 1 ", resultado);

        return soma;
    }
}
