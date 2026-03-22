package com.habts.routine.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

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
}
