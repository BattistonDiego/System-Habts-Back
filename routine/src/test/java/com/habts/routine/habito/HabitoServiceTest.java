package com.habts.routine.habito;

import com.habts.routine.exception.RecursoExistenteException;
import com.habts.routine.exception.RecursoNaoEncontradoException;
import com.habts.routine.habito.dtos.DetalhesCadastro;
import com.habts.routine.habito.dtos.DtoResponse;
import com.habts.routine.habito.dtos.HabitoResponse;
import com.habts.routine.habitoHistory.HistoricoRepository;
import com.habts.routine.users.Usuario;
import com.habts.routine.users.UsuarioRepository;
import com.habts.routine.users.enums.PerfilUsuario;
import com.habts.routine.users.enums.StatusUsuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
public class HabitoServiceTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private HabitoRepository habitoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private HabitoService habitoService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void deveExcluirHabito() {
        // Arrange
        Usuario usuario = new Usuario(1L, "Diego", "diego@email.com", "11999999999", "Testes", StatusUsuario.ATIVO, PerfilUsuario.USUARIO);
        Long id = 1L;
        Habito habito = new Habito(
                usuario,
                "Beber água",
                8,
                "copos",
                "💧",
                "#2196F3",
                0
        );
        when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));

        // Act
        ResponseEntity response = habitoService.deleteHabito(id);

        // Assert
        verify(historicoRepository, times(1)).deleteByHabitoId(id);
        verify(habitoRepository, times(1)).deleteById(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deveLancarExceptionQuandoNaoEncontrarHabito() {
        Long id = 1L;
        when(habitoRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            habitoService.deleteHabito(id);
        });

    }

    @Test
    void deveCriarHabito(){
        Usuario usuario = new Usuario(1L, "Diego", "diego@email.com", "11999999999", "Testes", StatusUsuario.ATIVO, PerfilUsuario.USUARIO);
        DetalhesCadastro dto = new DetalhesCadastro(
                1L,             // usuarioId
                "Beber água",   // nome
                8,              // meta
                "copos",        // unidade
                "💧",           // icone
                "#2196F3"       // cor
        );
        Habito habitoFake = new Habito(
                usuario,
                dto.nome(),
                dto.meta(),
                dto.unidade(),
                dto.icone(),
                dto.cor(),
                1
        );
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitoRepository.countByUsuarioId(usuario.getId())).thenReturn(1);
        when(habitoRepository.save(any(Habito.class))).thenReturn(habitoFake);

        ResponseEntity<DtoResponse> response = habitoService.createHabito(dto);

        assertEquals("Beber água", response.getBody().nomeHabito());
        assertEquals(8, response.getBody().meta());

    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste(){
        Long id = 99L;
        DetalhesCadastro dto = new DetalhesCadastro(
                id,             // usuarioId
                "Beber água",   // nome
                8,              // meta
                "copos",        // unidade
                "💧",           // icone
                "#2196F3"       // cor
        );


        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            habitoService.createHabito(dto);
        });
    }

    @Test
    void deveAtualizarHabito(){
        Long id = 1L;
        DetalhesCadastro dto = new DetalhesCadastro(
                id,             // usuarioId
                "Teste",   // nome
                8,              // meta
                "copos",        // unidade
                "💧",           // icone
                "#2196F3"       // cor
        );

        Usuario usuario = new Usuario(1L, "Diego", "diego@email.com", "11999999999", "Testes", StatusUsuario.ATIVO, PerfilUsuario.USUARIO);

        Habito habito = new Habito(
                usuario,
                "Beber água",
                8,
                "copos",
                "💧",
                "#2196F3",
                0
        );


        when(habitoRepository.findById(id)).thenReturn(Optional.of(habito));

        when(habitoRepository.save(any(Habito.class))).thenReturn(habito);

        ResponseEntity<Habito> resultado = habitoService.editarHabito(id, dto);

        assertEquals(dto.nome(), resultado.getBody().getNome());
    }

    @Test
    void deveLancarExcecaoQuandoHabitoNaoExisteAoEditar(){
        Long id = 1L;
        DetalhesCadastro dto = new DetalhesCadastro(
                id,             // usuarioId
                "Teste",   // nome
                8,              // meta
                "copos",        // unidade
                "💧",           // icone
                "#2196F3"       // cor
        );


        when(habitoRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            habitoService.editarHabito(id, dto);
        });

    }

    @Test
    void deveTrazerTodosOsHabitos(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Usuario usuario = new Usuario(null, "usuario@teste.com", "12345678" );

        Habito habito1 = new Habito(usuario, "Beber água", 8, "copos", "💧", "#2196F3", 0);
        habito1.setId(1L);

        Habito habito2 = new Habito(usuario, "Ler", 30, "minutos", "📖", "#4CAF50", 1);
        habito2.setId(2L);

        List<Habito> listaHabitos = List.of(habito1, habito2);

        // Act
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario@teste.com");
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(habitoRepository.findByUsuarioIdOrderByOrdemAsc(usuario.getId())).thenReturn(listaHabitos);

        ResponseEntity<List<HabitoResponse>> resultado = habitoService.listAllHabito();

        assertEquals(listaHabitos.size(), resultado.getBody().size());
        assertEquals("Ler", resultado.getBody().get(1).nome());

    }

    @Test
    void deveLancarExceptionSeUsuarioNaoExistir(){
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario@teste.com");
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> {
            habitoService.listAllHabito();
        });
    }
}
