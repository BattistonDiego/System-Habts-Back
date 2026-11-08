package com.habts.routine.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habts.routine.users.dtos.DtoRequestUser;
import com.habts.routine.users.enums.PerfilUsuario;
import com.habts.routine.users.enums.StatusUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {
        // Arrange
        DtoRequestUser dto = new DtoRequestUser(
                "Diego Teste",
                "diego.teste1@email.com",
                "senha123",
                "11999999999",
                PerfilUsuario.USUARIO,
                StatusUsuario.ATIVO
        );

        String json = objectMapper.writeValueAsString(dto);

        // Act + Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("diego.teste1@email.com"));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() throws Exception {
        // Arrange
        DtoRequestUser dto = new DtoRequestUser(
                "Diego Teste",
                "diego.teste2@email.com",
                "senha123",
                "11999999999",
                PerfilUsuario.USUARIO,
                StatusUsuario.ATIVO
        );

        String json = objectMapper.writeValueAsString(dto);

        // Act + Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(status().isCreated());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

    }
}