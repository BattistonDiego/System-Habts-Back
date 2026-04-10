package com.habts.routine.authentication;

import com.habts.routine.users.enums.PerfilUsuario;

public record LoginRequest(String email, String senha) {
}
