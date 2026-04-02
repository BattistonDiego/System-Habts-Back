package com.habts.routine.users.dtos;

import com.habts.routine.users.enums.PerfilUsuario;
import com.habts.routine.users.enums.StatusUsuario;

public record DtoRequestUser(String nome, String email, String senha, String telefone, PerfilUsuario perfil, StatusUsuario status) {
}
