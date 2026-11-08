package com.habts.routine.users;

import com.habts.routine.exception.RecursoExistenteException;
import com.habts.routine.exception.RecursoNaoEncontradoException;
import com.habts.routine.users.dtos.DtoRequestUser;
import com.habts.routine.users.dtos.DtoResponseUser;
import com.habts.routine.users.enums.StatusUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<DtoResponseUser> createUser( DtoRequestUser usuario) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(usuario.email());
        if (usuarioOptional.isPresent()) {
            throw new RecursoExistenteException("Usuario ja esta cadastrado");
        }

        Usuario user = new Usuario(null, usuario.nome(), usuario.email(), usuario.telefone(), passwordEncoder.encode(usuario.senha()), usuario.status(), usuario.perfil());

        Usuario userNew =  usuarioRepository.save(user);

        return  ResponseEntity.status(HttpStatus.CREATED).body(DtoResponseUser.fromEntity(userNew));
    }

    public ResponseEntity<DtoResponseUser> getById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com id: " + id));

        return ResponseEntity.ok(DtoResponseUser.fromEntity(usuario));
    }

    public DtoResponseUser userLogged(Authentication authentication){
        String email = authentication.getName();

        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var usuario = new DtoResponseUser(user.getId(), user.getNome(), user.getEmail(), user.getTelefone(), user.getStatus().name(), user.getPerfil().name());

        return usuario;
    }

    public Page<DtoResponseUser> getAll(StatusUsuario status, Pageable pageable) {
        Page<Usuario> page;

        if(status != null) {
            page = usuarioRepository.findByStatus(status, pageable);
        }else{
            page = usuarioRepository.findAll(pageable);
        }
        return page.map(DtoResponseUser::fromEntity);
    }
}
