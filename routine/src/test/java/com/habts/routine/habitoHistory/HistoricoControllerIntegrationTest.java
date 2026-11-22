package com.habts.routine.habitoHistory;

import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import com.habts.routine.infrastructure.security.JwtService;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import com.habts.routine.users.enums.PerfilUsuario;
import com.habts.routine.users.enums.StatusUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class HistoricoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HabitoRepository habitoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private Usuario criarUsuario() {
        String emailUnico = "calendario+" + UUID.randomUUID() + "@email.com";
        Usuario usuario = new Usuario(null, "Diego", emailUnico, "555855280",
                passwordEncoder.encode("senha123"), StatusUsuario.ATIVO, PerfilUsuario.USUARIO);
        return usuarioRepository.save(usuario);
    }

    private Habito criarHabito(Usuario usuario, String nome, String descricao, String color, String icon) {
        return habitoRepository.save(new Habito(usuario, nome, icon, color, descricao));
    }

    private String bearer(Usuario usuario) {
        return "Bearer " + jwtService.gerarToken(usuario.getEmail());
    }

    @Test
    void deveRetornarHabitosEConclusoesDoMesDoUsuarioLogado() throws Exception {
        YearMonth mesAtual = YearMonth.now();
        Usuario usuario = criarUsuario();
        Habito java = criarHabito(usuario, "Estudar Java", "Estudar Java por 1 hora", "#3b82f6", "assets/svg/icon-code.svg");
        Habito agua = criarHabito(usuario, "Beber água", "Beber 2 litros de água", "#22d3ee", "assets/svg/icon-water.svg");

        // Entra: conclusão no dia 1 do mês atual
        historicoRepository.save(new Historico(null, java, mesAtual.atDay(1), true));
        // Fica de fora: registro não concluído
        historicoRepository.save(new Historico(null, agua, mesAtual.atDay(1), false));
        // Fica de fora: conclusão no mês anterior
        historicoRepository.save(new Historico(null, java, mesAtual.minusMonths(1).atEndOfMonth(), true));

        // Fica de fora: hábito de outro usuário
        Usuario outro = criarUsuario();
        Habito habitoDeOutro = criarHabito(outro, "Leitura", "Ler 20 páginas", "#10b981", "assets/png/icon-study.png");
        historicoRepository.save(new Historico(null, habitoDeOutro, mesAtual.atDay(1), true));

        mockMvc.perform(get("/historico/mes")
                        .param("ano", String.valueOf(mesAtual.getYear()))
                        .param("mes", String.valueOf(mesAtual.getMonthValue()))
                        .header("Authorization", bearer(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ano").value(mesAtual.getYear()))
                .andExpect(jsonPath("$.mes").value(mesAtual.getMonthValue()))
                .andExpect(jsonPath("$.habitos", hasSize(2)))
                .andExpect(jsonPath("$.habitos[0].id").value(java.getId()))
                .andExpect(jsonPath("$.habitos[0].nome").value("Estudar Java"))
                .andExpect(jsonPath("$.habitos[0].descricao").value("Estudar Java por 1 hora"))
                .andExpect(jsonPath("$.habitos[0].color").value("#3b82f6"))
                .andExpect(jsonPath("$.habitos[0].icon").value("assets/svg/icon-code.svg"))
                .andExpect(jsonPath("$.habitos[0].meta").doesNotExist())
                .andExpect(jsonPath("$.habitos[1].id").value(agua.getId()))
                .andExpect(jsonPath("$.conclusoes", hasSize(1)))
                .andExpect(jsonPath("$.conclusoes[0].habitoId").value(java.getId()))
                .andExpect(jsonPath("$.conclusoes[0].data").value(mesAtual.atDay(1).toString()));
    }

    @Test
    void deveRetornar400QuandoMesForFuturo() throws Exception {
        YearMonth proximoMes = YearMonth.now().plusMonths(1);
        Usuario usuario = criarUsuario();

        mockMvc.perform(get("/historico/mes")
                        .param("ano", String.valueOf(proximoMes.getYear()))
                        .param("mes", String.valueOf(proximoMes.getMonthValue()))
                        .header("Authorization", bearer(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Não é possível consultar meses futuros"));
    }
}
