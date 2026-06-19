package Gl1tch_st0re.preventas.servicio;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.exceptions.preventasNotFoundException;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.repositorio.preventasRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de preventas.
 *
 * Se mockea el repositorio JPA para aislar y validar exclusivamente
 * la lógica de negocio contenida en preventasServicio: normalización
 * del estado a mayúsculas, búsquedas filtradas por usuario/estado
 * y manejo de preventas inexistentes (sin levantar contexto de Spring
 * ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("preventasServicio - pruebas unitarias")
class preventasServicioTest {

    @Mock
    private preventasRepositorio preventasRepositorio;

    @InjectMocks
    private preventasServicio preventasServicio;

    private preventasModelo preventaExistente;
    private preventasRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        preventaExistente = preventasModelo.builder()
                .id(1L)
                .usuario("ana.garcia")
                .producto("GPU RTX 5090")
                .cantidad(1)
                .estado("RESERVADO")
                .fechaReserva(LocalDateTime.now())
                .fechaLanzamiento(LocalDateTime.now().plusMonths(3))
                .build();

        dtoValido = new preventasRequestDTO();
        dtoValido.setUsuario("carlos.rojas");
        dtoValido.setProducto("CPU Ryzen 9 8950X");
        dtoValido.setCantidad(2);
        dtoValido.setEstado("reservado");
        dtoValido.setFechaLanzamiento(LocalDateTime.now().plusMonths(6));
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todas las preventas registradas")
    void findAll_retornaListaCompleta() {
        preventasModelo otra = preventasModelo.builder().id(2L).usuario("luis").build();
        when(preventasRepositorio.findAll()).thenReturn(List.of(preventaExistente, otra));

        List<preventasModelo> resultado = preventasServicio.findAll();

        assertEquals(2, resultado.size());
        verify(preventasRepositorio, times(1)).findAll();
    }

    // ---------- findById ----------

    @Test
    @DisplayName("findById retorna la preventa cuando existe")
    void findById_existente_retornaPreventa() {
        when(preventasRepositorio.findById(1L)).thenReturn(Optional.of(preventaExistente));

        preventasModelo resultado = preventasServicio.findById(1L);

        assertNotNull(resultado);
        assertEquals("ana.garcia", resultado.getUsuario());
        assertEquals("GPU RTX 5090", resultado.getProducto());
    }

    @Test
    @DisplayName("findById lanza preventasNotFoundException cuando no existe")
    void findById_inexistente_lanzaExcepcion() {
        when(preventasRepositorio.findById(99L)).thenReturn(Optional.empty());

        preventasNotFoundException ex = assertThrows(preventasNotFoundException.class,
                () -> preventasServicio.findById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- findByUsuario ----------

    @Test
    @DisplayName("findByUsuario retorna las preventas del usuario cuando existen")
    void findByUsuario_conResultados_retornaLista() {
        when(preventasRepositorio.findByUsuario("ana.garcia")).thenReturn(List.of(preventaExistente));

        List<preventasModelo> resultado = preventasServicio.findByUsuario("ana.garcia");

        assertEquals(1, resultado.size());
        assertEquals("ana.garcia", resultado.get(0).getUsuario());
    }

    @Test
    @DisplayName("findByUsuario lanza excepción cuando el usuario no tiene preventas")
    void findByUsuario_sinResultados_lanzaExcepcion() {
        when(preventasRepositorio.findByUsuario("sin.preventas")).thenReturn(List.of());

        assertThrows(preventasNotFoundException.class,
                () -> preventasServicio.findByUsuario("sin.preventas"));
    }

    // ---------- findByEstado ----------

    @Test
    @DisplayName("findByEstado normaliza el estado a mayúsculas antes de consultar")
    void findByEstado_normalizaAMayusculas() {
        when(preventasRepositorio.findByEstado("RESERVADO")).thenReturn(List.of(preventaExistente));

        List<preventasModelo> resultado = preventasServicio.findByEstado("reservado");

        assertEquals(1, resultado.size());
        verify(preventasRepositorio).findByEstado("RESERVADO");
    }

    @Test
    @DisplayName("findByEstado lanza excepción cuando no hay preventas con ese estado")
    void findByEstado_sinResultados_lanzaExcepcion() {
        when(preventasRepositorio.findByEstado("CANCELADO")).thenReturn(List.of());

        assertThrows(preventasNotFoundException.class,
                () -> preventasServicio.findByEstado("cancelado"));
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear normaliza el estado a mayúsculas y guarda la preventa correctamente")
    void crear_casoExitoso() {
        when(preventasRepositorio.save(any(preventasModelo.class))).thenAnswer(invocacion -> {
            preventasModelo modelo = invocacion.getArgument(0);
            modelo.setId(10L);
            return modelo;
        });

        preventasModelo resultado = preventasServicio.crear(dtoValido);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("carlos.rojas", resultado.getUsuario());
        assertEquals("CPU Ryzen 9 8950X", resultado.getProducto());
        assertEquals("RESERVADO", resultado.getEstado(), "El estado debe normalizarse a mayúsculas");
        assertEquals(2, resultado.getCantidad());
        assertNotNull(resultado.getFechaReserva(), "La fecha de reserva debe asignarse automáticamente");
        verify(preventasRepositorio, times(1)).save(any(preventasModelo.class));
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza todos los campos de la preventa y normaliza el estado")
        void actualizar_casoExitoso() {
            when(preventasRepositorio.findById(1L)).thenReturn(Optional.of(preventaExistente));
            when(preventasRepositorio.save(any(preventasModelo.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            preventasRequestDTO dtoActualizacion = new preventasRequestDTO();
            dtoActualizacion.setUsuario("ana.garcia.v2");
            dtoActualizacion.setProducto("GPU RTX 5090 Ti");
            dtoActualizacion.setCantidad(3);
            dtoActualizacion.setEstado("confirmado");
            dtoActualizacion.setFechaLanzamiento(LocalDateTime.now().plusMonths(4));

            preventasModelo resultado = preventasServicio.actualizar(1L, dtoActualizacion);

            assertEquals("ana.garcia.v2", resultado.getUsuario());
            assertEquals("GPU RTX 5090 Ti", resultado.getProducto());
            assertEquals(3, resultado.getCantidad());
            assertEquals("CONFIRMADO", resultado.getEstado(), "El estado debe normalizarse a mayúsculas");
            verify(preventasRepositorio, times(1)).save(preventaExistente);
        }

        @Test
        @DisplayName("lanza excepción cuando la preventa a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(preventasRepositorio.findById(99L)).thenReturn(Optional.empty());

            assertThrows(preventasNotFoundException.class,
                    () -> preventasServicio.actualizar(99L, dtoValido));
            verify(preventasRepositorio, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra la preventa existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        when(preventasRepositorio.findById(1L)).thenReturn(Optional.of(preventaExistente));

        String resultado = preventasServicio.eliminar(1L);

        assertTrue(resultado.contains("ana.garcia"));
        assertTrue(resultado.contains("GPU RTX 5090"));
        verify(preventasRepositorio, times(1)).delete(preventaExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si la preventa no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(preventasRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(preventasNotFoundException.class, () -> preventasServicio.eliminar(99L));
        verify(preventasRepositorio, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todas las preventas y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(preventasRepositorio.count()).thenReturn(7L);

        String resultado = preventasServicio.eliminarTodos();

        assertTrue(resultado.contains("7"));
        verify(preventasRepositorio, times(1)).deleteAll();
    }
}
