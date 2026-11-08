package com.habts.routine.habito;

import com.habts.routine.exception.RecursoNaoEncontradoException;
import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.habito.dtos.DtoResponse;
import com.habts.routine.habito.dtos.HabitoResponse;
import com.habts.routine.habitoHistory.HistoricoRepository;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitoService {

    private final HabitoRepository habitoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoRepository historicoRepository;

    public HabitoService(HabitoRepository habitoRepository, UsuarioRepository usuarioRepository,  HistoricoRepository historicoRepository) {
        this.habitoRepository = habitoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoRepository = historicoRepository;
    }


    public ResponseEntity<DtoResponse> createHabito(DetalhesCadastro dto){
        Usuario usuario = usuarioRepository.findById(dto.usuarioId()).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        int totalHabitos = habitoRepository.countByUsuarioId(usuario.getId());
        var newHabito = new Habito(usuario, dto.nome(), dto.meta(), dto.unidade(), dto.icone(), dto.cor(), totalHabitos);
        habitoRepository.save(newHabito);


        return ResponseEntity.ok(DtoResponse.fromHabito(newHabito));
    }

    public ResponseEntity<List<HabitoResponse>> listAllHabito(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var userLogged = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(userLogged)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        var listHabitos = habitoRepository.findByUsuarioIdOrderByOrdemAsc(user.getId()).stream().map(h -> new HabitoResponse(h.getId(), h.getNome(), h.getMeta(), h.getUnidade(), h.getIcone(), h.getCor(), h.getOrdem())).toList();

        return ResponseEntity.ok(listHabitos);
    }

    public ResponseEntity deleteHabito(Long id){
        Habito habito = habitoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Habito não encontrado"));

        historicoRepository.deleteByHabitoId(id);
        habitoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Habito> editarHabito(Long id, DetalhesCadastro dto){
        Habito habito = habitoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Habito não encontrado"));;

        if(dto.nome() != null){
            habito.setNome(dto.nome());
        }

        if (dto.meta() != null){
            habito.setMeta(dto.meta());
        }

        if (dto.unidade() != null){
            habito.setUnidade(dto.unidade());
        }
        if (dto.icone() != null){
            habito.setIcone(dto.icone());
        }
        if (dto.cor() != null){
            habito.setCor(dto.cor());
        }

        var habitEdited = habitoRepository.save(habito);

        return ResponseEntity.ok(habitEdited);
    }
}
