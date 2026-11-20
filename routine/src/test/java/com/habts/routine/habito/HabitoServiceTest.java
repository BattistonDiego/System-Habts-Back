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
import static org.junit.jupiter.api.Assertions.assertSame;
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
                "💧",
                "#2196F3",
                "Descrição do hábito"
        );
        when(habitoRepository.findById(1L)).thenReturn(Optional.of(habito));

        // Act
        ResponseEntity response = habitoService.deleteHabito(id);

        // Assert
        verify(historicoRepository, times(1)).deleteByHabito_Id(id);
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
                "💧",           // icon
                "#2196F3",      // color
                "Beber mais água" // descricao
        );
        Habito habitoFake = new Habito(
                usuario,
                dto.nome(),
                dto.icon(),
                dto.color(),
                dto.descricao()
        );
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitoRepository.save(any(Habito.class))).thenReturn(habitoFake);

        ResponseEntity<DtoResponse> response = habitoService.createHabito(dto);

        assertEquals("Beber água", response.getBody().nomeHabito());
        assertEquals("💧", response.getBody().icon());
        assertEquals("Beber mais água", response.getBody().descricao());

    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste(){
        Long id = 99L;
        DetalhesCadastro dto = new DetalhesCadastro(
                id,             // usuarioId
                "Beber água",   // nome
                "💧",           // icon
                "#2196F3",      // color
                "Descrição do hábito" // descricao
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
                "💧",           // icon
                "#2196F3",      // color
                "Descrição atualizada" // descricao
        );

        Usuario usuario = new Usuario(1L, "Diego", "diego@email.com", "11999999999", "Testes", StatusUsuario.ATIVO, PerfilUsuario.USUARIO);

        Habito habito = new Habito(
                usuario,
                "Beber água",
                "💧",
                "#2196F3",
                "Descrição anterior"
        );


        when(habitoRepository.findById(id)).thenReturn(Optional.of(habito));

        when(habitoRepository.save(any(Habito.class))).thenReturn(habito);

        ResponseEntity<Habito> resultado = habitoService.editarHabito(id, dto);

        assertEquals(dto.nome(), resultado.getBody().getNome());
        assertEquals(dto.icon(), resultado.getBody().getIcon());
        assertEquals(dto.color(), resultado.getBody().getColor());
        assertEquals(dto.descricao(), resultado.getBody().getDescricao());
        assertSame(usuario, resultado.getBody().getUsuario());
        // Updating habit metadata must not query, create, or delete daily completion history.
        verifyNoInteractions(historicoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoHabitoNaoExisteAoEditar(){
        Long id = 1L;
        DetalhesCadastro dto = new DetalhesCadastro(
                id,             // usuarioId
                "Teste",   // nome
                "💧",           // icon
                "#2196F3",      // color
                "Descrição do hábito" // descricao
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

        Habito habito1 = new Habito(usuario, "Beber água", "💧", "#2196F3", "Tomar água diariamente");
        habito1.setId(1L);

        Habito habito2 = new Habito(usuario, "Ler", "📖", "#4CAF50", "Ler um pouco todos os dias");
        habito2.setId(2L);

        List<Habito> listaHabitos = List.of(habito1, habito2);

        // Act
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario@teste.com");
        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(usuario));
        when(habitoRepository.findByUsuarioId(usuario.getId())).thenReturn(listaHabitos);

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
