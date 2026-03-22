package com.habts.routine.users;

public record DtoResponseUser(Long id,
                              String nome,
                              String email,
                              String telefone,
                              String status,
                              String perfil) {

    public static DtoResponseUser fromEntity(Usuario user) {
        return new DtoResponseUser(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getTelefone(),
                user.getStatus().name(),
                user.getPerfil().name()
        );
    }
}
