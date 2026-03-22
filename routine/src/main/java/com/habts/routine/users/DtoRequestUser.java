package com.habts.routine.users;

public record DtoRequestUser(String nome, String email, String senha, String telefone, PerfilUsuario perfil, StatusUsuario status) {
}
