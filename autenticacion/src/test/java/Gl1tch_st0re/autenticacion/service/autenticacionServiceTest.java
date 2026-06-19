package Gl1tch_st0re.autenticacion.service;

import Gl1tch_st0re.autenticacion.dto.request.actualizarUsuarioRequestDTO;
import Gl1tch_st0re.autenticacion.dto.request.autenticacionRequestDTO;
import Gl1tch_st0re.autenticacion.exceptions.AutenticacionNotFoundException;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import Gl1tch_st0re.autenticacion.repository.autenticacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de autenticación.
 *
 * Se mockean el repositorio JPA y el PasswordEncoder de Spring Security
 * para aislar y validar exclusivamente la lógica de negocio contenida
 * en autenticacionService (sin levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("autenticacionService - pruebas unitarias")
class autenticacionServiceTest {

    @Mock
    private autenticacionRepository autenticacionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private autenticacionService autenticacionService;

    private autenticacionModel usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioExistente = autenticacionModel.builder()
                .id(1L)
                .usuario("juan.perez")
                .password("HASH_GUARDADO")
                .correo("juan.perez@mail.com")
                .build();
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los usuarios registrados")
    void findAll_retornaListaCompleta() {
        autenticacionModel otro = autenticacionModel.builder().id(2L).usuario("ana").build();
        when(autenticacionRepository.findAll()).thenReturn(List.of(usuarioExistente, otro));

        List<autenticacionModel> resultado = autenticacionService.findAll();

        assertEquals(2, resultado.size());
        verify(autenticacionRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna el usuario cuando existe")
    void obtenerPorId_existente_retornaUsuario() {
        when(autenticacionRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        autenticacionModel resultado = autenticacionService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("juan.perez", resultado.getUsuario());
    }

    @Test
    @DisplayName("obtenerPorId lanza AutenticacionNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(autenticacionRepository.findById(99L)).thenReturn(Optional.empty());

        AutenticacionNotFoundException ex = assertThrows(AutenticacionNotFoundException.class,
                () -> autenticacionService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- validarCredenciales ----------

    @Nested
    @DisplayName("validarCredenciales()")
    class ValidarCredenciales {

        @Test
        @DisplayName("retorna false cuando el usuario no existe")
        void usuarioInexistente_retornaFalse() {
            when(autenticacionRepository.findByUsuario("desconocido")).thenReturn(Optional.empty());

            boolean resultado = autenticacionService.validarCredenciales("desconocido", "cualquiera");

            assertFalse(resultado);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("retorna true cuando la contraseña coincide con el hash almacenado")
        void passwordCorrecta_retornaTrue() {
            when(autenticacionRepository.findByUsuario("juan.perez")).thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches("plain123", "HASH_GUARDADO")).thenReturn(true);

            boolean resultado = autenticacionService.validarCredenciales("juan.perez", "plain123");

            assertTrue(resultado);
        }

        @Test
        @DisplayName("retorna false cuando la contraseña no coincide con el hash almacenado")
        void passwordIncorrecta_retornaFalse() {
            when(autenticacionRepository.findByUsuario("juan.perez")).thenReturn(Optional.of(usuarioExistente));
            when(passwordEncoder.matches("incorrecta", "HASH_GUARDADO")).thenReturn(false);

            boolean resultado = autenticacionService.validarCredenciales("juan.perez", "incorrecta");

            assertFalse(resultado);
        }
    }

    // ---------- crearUsuario ----------

    @Nested
    @DisplayName("crearUsuario()")
    class CrearUsuario {

        @Test
        @DisplayName("crea el usuario encriptando la contraseña antes de guardarla")
        void crear_casoExitoso() {
            autenticacionRequestDTO dto = new autenticacionRequestDTO();
            dto.setUsuario("nuevo.usuario");
            dto.setPassword("plain123");
            dto.setCorreo("nuevo@mail.com");

            when(passwordEncoder.encode("plain123")).thenReturn("ENCODED_plain123");
            // save() asigna el id, simulando el comportamiento de JPA con GenerationType.IDENTITY
            when(autenticacionRepository.save(any(autenticacionModel.class))).thenAnswer(invocacion -> {
                autenticacionModel modelo = invocacion.getArgument(0);
                modelo.setId(1L);
                return modelo;
            });
            autenticacionModel guardadoEnBd = autenticacionModel.builder()
                    .id(1L).usuario("nuevo.usuario").password("ENCODED_plain123").correo("nuevo@mail.com").build();
            when(autenticacionRepository.findById(1L)).thenReturn(Optional.of(guardadoEnBd));

            autenticacionModel resultado = autenticacionService.crearUsuario(dto);

            assertNotNull(resultado);
            assertEquals("nuevo.usuario", resultado.getUsuario());
            assertEquals("ENCODED_plain123", resultado.getPassword(), "La contraseña nunca debe guardarse en texto plano");
            assertEquals("nuevo@mail.com", resultado.getCorreo());
            verify(passwordEncoder, times(1)).encode("plain123");
            verify(autenticacionRepository, times(1)).save(any(autenticacionModel.class));
            verify(autenticacionRepository, times(1)).flush();
        }

        @Test
        @DisplayName("lanza excepción si el usuario recién guardado no se puede recuperar")
        void crear_noEncontradoTrasGuardar_lanzaExcepcion() {
            autenticacionRequestDTO dto = new autenticacionRequestDTO();
            dto.setUsuario("fantasma");
            dto.setPassword("plain123");
            dto.setCorreo("fantasma@mail.com");

            when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
            when(autenticacionRepository.save(any(autenticacionModel.class))).thenAnswer(invocacion -> {
                autenticacionModel modelo = invocacion.getArgument(0);
                modelo.setId(5L);
                return modelo;
            });
            when(autenticacionRepository.findById(5L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> autenticacionService.crearUsuario(dto));
        }
    }

    // ---------- actualizarUsuario ----------

    @Test
    @DisplayName("actualizarUsuario modifica usuario y re-encripta la contraseña")
    void actualizar_casoExitoso() {
        when(autenticacionRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("nuevaPlain")).thenReturn("ENCODED_nuevaPlain");
        when(autenticacionRepository.save(any(autenticacionModel.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        actualizarUsuarioRequestDTO dto = new actualizarUsuarioRequestDTO();
        dto.setUsuario("juan.perez.actualizado");
        dto.setPassword("nuevaPlain");

        autenticacionModel resultado = autenticacionService.actualizarUsuario(1L, dto);

        assertEquals("juan.perez.actualizado", resultado.getUsuario());
        assertEquals("ENCODED_nuevaPlain", resultado.getPassword());
        verify(autenticacionRepository, times(1)).save(usuarioExistente);
    }

    @Test
    @DisplayName("actualizarUsuario lanza excepción si el usuario no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(autenticacionRepository.findById(99L)).thenReturn(Optional.empty());

        actualizarUsuarioRequestDTO dto = new actualizarUsuarioRequestDTO();
        dto.setUsuario("x");
        dto.setPassword("y");

        assertThrows(AutenticacionNotFoundException.class,
                () -> autenticacionService.actualizarUsuario(99L, dto));
        verify(autenticacionRepository, never()).save(any());
    }

    // ---------- eliminarUsuario ----------

    @Test
    @DisplayName("eliminarUsuario borra el usuario existente")
    void eliminar_casoExitoso() {
        when(autenticacionRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        autenticacionService.eliminarUsuario(1L);

        verify(autenticacionRepository, times(1)).delete(usuarioExistente);
    }

    @Test
    @DisplayName("eliminarUsuario lanza excepción si el usuario no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(autenticacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AutenticacionNotFoundException.class,
                () -> autenticacionService.eliminarUsuario(99L));
        verify(autenticacionRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los usuarios registrados")
    void eliminarTodos_casoExitoso() {
        when(autenticacionRepository.count()).thenReturn(7L);

        autenticacionService.eliminarTodos();

        verify(autenticacionRepository, times(1)).deleteAll();
    }
}