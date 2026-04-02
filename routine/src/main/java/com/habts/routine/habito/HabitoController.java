package com.habts.routine.habito;

import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.habitoHistory.HistoricoRepository;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    @PostMapping
    public ResponseEntity cadastrarHabito(@RequestBody @Valid DetalhesCadastro dto){

        Usuario usuario = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var newHabito = new Habito(usuario, dto.nome(), dto.meta(), dto.unidade(), dto.icone(), dto.cor());
        var habtitsaved = habitoRepository.save(newHabito);

        return ResponseEntity.ok(habtitsaved);
    }

    @GetMapping
    public ResponseEntity<List<Habito>> listHabito(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var userLogged = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(userLogged).get();

        var listHabitos = habitoRepository.findByUsuarioId(user.getId());
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
