package com.habts.routine.habito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HabitoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveCriarUmHabitoComSucesso() throws Exception {
        String emailUnico = "diego.testeSenha+" + UUID.randomUUID() + "@email.com";
        Usuario usuarioExistente = new Usuario(null, "Diego", emailUnico, "555855280", passwordEncoder.encode("senha123"), StatusUsuario.ATIVO, PerfilUsuario.ADMINISTRADOR);
        usuarioExistente = usuarioRepository.save(usuarioExistente);
        DetalhesCadastro dto = new DetalhesCadastro(usuarioExistente.getId(), "Estudar", "#teste", "#FFF", "Estudar todos os dias");

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/habitos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeHabito").value("Estudar"))
                .andExpect(jsonPath("$.descricao").value("Estudar todos os dias"));

    }
}
