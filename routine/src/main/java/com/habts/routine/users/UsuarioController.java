package com.habts.routine.users;


import com.habts.routine.users.dtos.DtoRequestUser;
import com.habts.routine.users.dtos.DtoResponseUser;

import com.habts.routine.users.enums.StatusUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping
    public ResponseEntity<DtoResponseUser> createUser(@RequestBody DtoRequestUser usuario) {

        Optional<Usuario> userOptional = usuarioRepository.findByEmail(usuario.email());

        if (userOptional.isPresent()) {
            throw new RuntimeException("Usuario ja esta cadastrado");
        }

        Usuario user = new Usuario(null, usuario.nome(), usuario.email(), usuario.telefone(), passwordEncoder.encode(usuario.senha()), usuario.status(), usuario.perfil());

        Usuario savedUser = usuarioRepository.save(user);
        return  ResponseEntity.status(HttpStatus.CREATED).body(DtoResponseUser.fromEntity(user));

    }

    @GetMapping("/byId")
    public ResponseEntity<DtoResponseUser> getById(@RequestParam long id) {
        Optional<Usuario> userOptional = usuarioRepository.findById(id);

        if(userOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(DtoResponseUser.fromEntity(userOptional.get()));

    }


    @GetMapping("/me")
    public DtoResponseUser getUsuarioLogado(Authentication authentication) {
        String email = authentication.getName();

        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var usuario = new DtoResponseUser(user.getId(), user.getNome(), user.getEmail(), user.getTelefone(), user.getStatus().name(), user.getPerfil().name());

        return usuario;
    }

    @GetMapping
    public Page<DtoResponseUser> getAllUsers(Pageable pageable) {

        return usuarioRepository.findAll(pageable).map(DtoResponseUser::fromEntity);
    }


    @GetMapping("/resume")
    public Map<String, Long> getResume(){
        long ativos = usuarioRepository.countByStatus(StatusUsuario.ATIVO);
        long inativos = usuarioRepository.countByStatus(StatusUsuario.INATIVO);

        Map<String, Long> resume = new HashMap<>();
        resume.put("ativos", ativos);
        resume.put("inativos", inativos);

        return resume;
    }

    @PutMapping
    public ResponseEntity<DtoResponseUser> inativeUser(@RequestBody DtoRequestUser user) {
        Usuario usuario = usuarioRepository.findByEmail(user.email()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setStatus(StatusUsuario.INATIVO);


        return ResponseEntity.ok(DtoResponseUser.fromEntity(usuarioRepository.save(usuario)));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<DtoResponseUser> updateUser(@PathVariable Long id, @RequestBody DtoRequestUser usuario) {
        Optional<Usuario> userOptional = usuarioRepository.findById(id);

        if (userOptional.isEmpty()) {
            return  ResponseEntity.notFound().build();
        }

        Usuario user = userOptional.get();

        if(usuario.nome()!= null){
            user.setNome(usuario.nome());
        }

        if(usuario.email()!= null){
            user.setEmail(usuario.email());
        }
        if(usuario.senha()!= null){
            user.setSenha(passwordEncoder.encode(usuario.senha()));
        }

        if(usuario.perfil()!= null){
            user.setPerfil(usuario.perfil());
        }

        if(usuario.status()!= null){
            user.setStatus(usuario.status());
        }

        return ResponseEntity.ok(DtoResponseUser.fromEntity(usuarioRepository.save(user)));
    }


}
