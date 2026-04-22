package com.habts.routine.users;

import com.habts.routine.users.enums.StatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    long countByStatus(StatusUsuario status);

    Page<Usuario> findByStatus(StatusUsuario status, Pageable pageable);

}
