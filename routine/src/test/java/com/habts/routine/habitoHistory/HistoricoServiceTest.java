package com.habts.routine.habitoHistory;

import com.habts.routine.exception.RecursoExistenteException;
import com.habts.routine.exception.RecursoNaoEncontradoException;
import com.habts.routine.habito.Habito;
import com.habts.routine.habito.HabitoRepository;
import com.habts.routine.habitoHistory.dtos.DetalhesSave;
import com.habts.routine.habitoHistory.dtos.DtoAtividade;
import com.habts.routine.habitoHistory.dtos.DtoEstatisticaHabito;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private HabitoRepository habitoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private HistoricoService historicoService;

    @Test
    void deveLancarExceptionQuandoHabitoNaoExiste() {
        // Arrange
        DetalhesSave dto = new DetalhesSave(1L, true, 5);
        when(habitoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            historicoService.save(dto);
        });
    }

    @Test
    void deveLancarExceptionQuandoHabitoExistirNoHistorico() {
        // Arrange
        DetalhesSave dto = new DetalhesSave(1L, true, 5);
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);


        when(habitoRepository.findById(1L)).thenReturn(Optional.of(habitoFake));
        when(historicoRepository.existsByhabitoIdAndData(any(), any())).thenReturn(true);



        // Act + Assert
        assertThrows(RecursoExistenteException.class, () -> {
            historicoService.save(dto);
        });
    }

    @Test
    void deveSalvarOHabitoNoHistorico() {
        // Arrange
        DetalhesSave dto = new DetalhesSave(1L, true, 5);
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);
        Historico historicoFake = new Historico(1L, habitoFake, LocalDate.now(), true, 5, 8);

        when(habitoRepository.findById(1L)).thenReturn(Optional.of(habitoFake));
        when(historicoRepository.existsByhabitoIdAndData(any(), any())).thenReturn(false);
        when(historicoRepository.save(any())).thenReturn(historicoFake);

        // Act
        Historico resultado = historicoService.save(dto);

        // Assert
        assertEquals(historicoFake, resultado);
    }

    @Test
    void deveLancarExceptionQuandoHabitoNaoExisteAoCalcularEstatisticas() {
        when(habitoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            historicoService.calcularEstatisticas(1L);
        });
    }

    @Test
    void deveRetornarEstatisticasQuandoHabitoExistir() {
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);

        List<Historico> historicoList = List.of(
                new Historico(1L, habitoFake, LocalDate.now(), true, 8, 8),
                new Historico(2L, habitoFake, LocalDate.now().minusDays(1), true, 8, 8),
                new Historico(3L, habitoFake, LocalDate.now().minusDays(2), true, 8, 8)
        );

        when(habitoRepository.findById(1L)).thenReturn(Optional.of(habitoFake));
        when(historicoRepository.findByHabitoOrderByDataDesc(any())).thenReturn(historicoList);

        // Act
        DtoEstatisticaHabito resultado = historicoService.calcularEstatisticas(1L);

        // Assert
        assertEquals(3, resultado.totalRegistro());
        assertEquals(3, resultado.melhorSequencia());
        assertEquals(3, resultado.sequenciaAtual());
    }

    @Test
    void deveRetornarAtividadesQuandoUsuarioExistir() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Usuario usuarioFake = new Usuario(null, "usuario@teste.com", "12345678" );

        List<DtoAtividade> atividadesFake = List.of(
                new DtoAtividade(LocalDate.now(), 3L),
                new DtoAtividade(LocalDate.now().minusDays(1), 2L),
                new DtoAtividade(LocalDate.now().minusDays(2), 1L)
        );

        // Act
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario@teste.com");
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuarioFake));
        when(historicoRepository.buscarAtividade(any(), any())).thenReturn(atividadesFake);

        List<DtoAtividade> resultado = historicoService.buscarAtividades();

        // Assert
        assertEquals(3, resultado.size());
        assertEquals(2L, resultado.get(1).qntdHabitos());
    }

    @Test
    void deveLancarExceptionQuandoUsuarioNaoExisteAoBuscarAtividades() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario@teste.com");
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            historicoService.buscarAtividades();
        });
    }

    @Test
    void deveCalcularSequenciaAtual() {
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);

        List<Historico> historicoList = List.of(
                new Historico(1L, habitoFake, LocalDate.now(), true, 8, 8),
                new Historico(2L, habitoFake, LocalDate.now().minusDays(1), true, 8, 8),
                new Historico(3L, habitoFake, LocalDate.now().minusDays(2), true, 8, 8)
        );

        int resultado = historicoService.calcularSequenciaAtual(historicoList);

        assertEquals(3, resultado);

    }

    @Test
    void deveRetornarUmQuandoSequenciaEstiverQuebrada() {
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);

        List<Historico> historicoQuebrado = List.of(
                new Historico(1L, habitoFake, LocalDate.now(), true, 8, 8),
                new Historico(2L, habitoFake, LocalDate.now().minusDays(3), true, 8, 8) // pulo de 3 dias
        );

        int resultado = historicoService.calcularSequenciaAtual(historicoQuebrado);

        assertEquals(1, resultado);

    }

    @Test
    void deveCalcularMelhorSequencia() {
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);

        List<Historico> historicoMisto = List.of(
                new Historico(1L, habitoFake, LocalDate.now(), true, 8, 8),                  // hoje
                new Historico(2L, habitoFake, LocalDate.now().minusDays(1), true, 8, 8),     // ontem
                // --- quebra aqui ---
                new Historico(3L, habitoFake, LocalDate.now().minusDays(5), true, 8, 8),
                new Historico(4L, habitoFake, LocalDate.now().minusDays(6), true, 8, 8),
                new Historico(5L, habitoFake, LocalDate.now().minusDays(7), true, 8, 8),
                new Historico(6L, habitoFake, LocalDate.now().minusDays(8), true, 8, 8)
        );

        int resultado = historicoService.calcularMelhorSequencia(historicoMisto);

        assertEquals(4, resultado);

    }

    @Test
    void deveCalcularTaxaConclusao() {
        Habito habitoFake = new Habito(null, "Beber água", 8, "copos", "icone", "azul", 1);

        List<Historico> historicoList = List.of(
                new Historico(1L, habitoFake, LocalDate.now(), true, 8, 8),
                new Historico(2L, habitoFake, LocalDate.now().minusDays(1), true, 8, 8),
                new Historico(3L, habitoFake, LocalDate.now().minusDays(2), true, 8, 8)
        );

        int resultado = historicoService.calcularTaxaConclusao(historicoList);

        assertEquals(100, resultado);

    }

}