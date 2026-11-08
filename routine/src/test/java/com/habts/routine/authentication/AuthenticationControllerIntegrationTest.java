package com.habts.routine.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import com.habts.routine.users.dtos.DtoRequestUser;
import com.habts.routine.users.enums.PerfilUsuario;
import com.habts.routine.users.enums.StatusUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveFazerOLoginComSucesso() throws Exception {
        LoginRequest dto = new LoginRequest("diego.teste@email.com", "senha123");
        Usuario usuarioExistente = new Usuario(null, "Diego", "diego.teste@email.com", "555855280", passwordEncoder.encode("senha123"), StatusUsuario.ATIVO, PerfilUsuario.ADMINISTRADOR);
        usuarioRepository.save(usuarioExistente);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/habitos/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveRetornar401QuandoEmailNaoCadastrado() throws Exception {
        LoginRequest dto = new LoginRequest("diego.teste2@email.com", "senha123");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/habitos/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoSenhaIncorreta() throws Exception {
        LoginRequest dto = new LoginRequest("diego.testeSenha@email.com", "senha124");
        Usuario usuarioExistente = new Usuario(null, "Diego", "diego.testeSenha@email.com", "555855280", passwordEncoder.encode("senha123"), StatusUsuario.ATIVO, PerfilUsuario.ADMINISTRADOR);
        usuarioRepository.save(usuarioExistente);

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/habitos/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }


}
