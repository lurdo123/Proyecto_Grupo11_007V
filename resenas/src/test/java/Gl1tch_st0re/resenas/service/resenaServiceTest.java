package Gl1tch_st0re.resenas.service;

import Gl1tch_st0re.resenas.client.catalogoWebClient;
import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.resenas.exceptions.resenaNotFoundException;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.repository.resenaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de reseñas.
 *
 * Se mockean el repositorio JPA y el WebClient hacia el microservicio
 * catalogo para aislar y validar exclusivamente la lógica de negocio
 * contenida en resenaService: validación cruzada del producto, unicidad
 * de la pareja productoId/usuarioId tanto al crear como al actualizar
 * (sin levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("resenaService - pruebas unitarias")
class resenaServiceTest {

    @Mock
    private resenaRepository resenaRepository;

    @Mock
    private catalogoWebClient catalogoWebClient;

    @InjectMocks
    private resenaService resenaService;

    private resenaModel resenaExistente;
    private resenaRequestDTO dtoValido;
    private catalogoClienteDTO productoValido;

    @BeforeEach
    void setUp() {
        resenaExistente = resenaModel.builder()
                .id(1L)
                .productoId(10L)
                .usuarioId(100L)
                .calificacion(5)
                .comentario("Excelente producto, llegó rápido y en perfectas condiciones.")
                .esCompraVerificada(true)
                .fechaPublicacion(LocalDate.now())
                .build();

        dtoValido = new resenaRequestDTO();
        dtoValido.setProductoId(20L);
        dtoValido.setUsuarioId(200L);
        dtoValido.setCalificacion(4);
        dtoValido.setComentario("Muy buen producto, cumple con lo descrito.");
        dtoValido.setEsCompraVerificada(true);
        dtoValido.setFechaPublicacion(LocalDate.now());

        productoValido = new catalogoClienteDTO();
        productoValido.setId(20L);
        productoValido.setNombre("Monitor 27'' 144Hz");
        productoValido.setDisponible(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todas las reseñas registradas")
    void findAll_retornaListaCompleta() {
        resenaModel otra = resenaModel.builder().id(2L).productoId(30L).usuarioId(300L).build();
        when(resenaRepository.findAll()).thenReturn(List.of(resenaExistente, otra));

        List<resenaModel> resultado = resenaService.findAll();

        assertEquals(2, resultado.size());
        verify(resenaRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna la reseña cuando existe")
    void obtenerPorId_existente_retornaResena() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaExistente));

        resenaModel resultado = resenaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(5, resultado.getCalificacion());
        assertEquals(10L, resultado.getProductoId());
    }

    @Test
    @DisplayName("obtenerPorId lanza resenaNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        resenaNotFoundException ex = assertThrows(resenaNotFoundException.class,
                () -> resenaService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea la reseña cuando el producto existe en catálogo y la pareja usuario/producto no está duplicada")
        void crear_casoExitoso() {
            when(catalogoWebClient.obtenerProducto(20L, "Bearer token123")).thenReturn(productoValido);
            when(resenaRepository.existsByProductoIdAndUsuarioId(20L, 200L)).thenReturn(false);
            when(resenaRepository.save(any(resenaModel.class))).thenAnswer(invocacion -> {
                resenaModel modelo = invocacion.getArgument(0);
                modelo.setId(5L);
                return modelo;
            });

            resenaModel resultado = resenaService.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals(20L, resultado.getProductoId());
            assertEquals(200L, resultado.getUsuarioId());
            assertEquals(4, resultado.getCalificacion());
            assertTrue(resultado.getEsCompraVerificada());
            verify(resenaRepository, times(1)).save(any(resenaModel.class));
        }

        @Test
        @DisplayName("lanza excepción cuando el producto no existe en catálogo (falla el WebClient)")
        void crear_productoNoEncontrado_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde catálogo"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> resenaService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrado en catálogo"));
            verify(resenaRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el WebClient retorna un producto nulo")
        void crear_productoNulo_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(20L, "Bearer token123")).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> resenaService.crear(dtoValido, "Bearer token123"));
            verify(resenaRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el usuario ya tiene una reseña para ese producto")
        void crear_parejaDuplicada_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(20L, "Bearer token123")).thenReturn(productoValido);
            when(resenaRepository.existsByProductoIdAndUsuarioId(20L, 200L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> resenaService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("Ya existe una resena"));
            verify(resenaRepository, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza la reseña cuando la nueva pareja usuario/producto no pertenece a otra reseña")
        void actualizar_casoExitoso() {
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaExistente));
            when(resenaRepository.existsByProductoIdAndUsuarioIdAndIdNot(20L, 200L, 1L)).thenReturn(false);
            when(resenaRepository.save(any(resenaModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            resenaModel resultado = resenaService.actualizar(1L, dtoValido);

            assertEquals(20L, resultado.getProductoId());
            assertEquals(200L, resultado.getUsuarioId());
            assertEquals(4, resultado.getCalificacion());
            assertEquals("Muy buen producto, cumple con lo descrito.", resultado.getComentario());
            verify(resenaRepository, times(1)).save(resenaExistente);
        }

        @Test
        @DisplayName("lanza excepción cuando la nueva pareja usuario/producto ya pertenece a otra reseña")
        void actualizar_parejaDuplicadaEnOtraResena_lanzaExcepcion() {
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaExistente));
            when(resenaRepository.existsByProductoIdAndUsuarioIdAndIdNot(20L, 200L, 1L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> resenaService.actualizar(1L, dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe otra resena"));
            verify(resenaRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando la reseña a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(resenaNotFoundException.class,
                    () -> resenaService.actualizar(99L, dtoValido));
            verify(resenaRepository, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra la reseña existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaExistente));

        String resultado = resenaService.eliminar(1L);

        assertTrue(resultado.contains("10"));
        assertTrue(resultado.contains("100"));
        assertTrue(resultado.contains("5"));
        verify(resenaRepository, times(1)).delete(resenaExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si la reseña no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(resenaNotFoundException.class, () -> resenaService.eliminar(99L));
        verify(resenaRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todas las reseñas y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(resenaRepository.count()).thenReturn(14L);

        String resultado = resenaService.eliminarTodos();

        assertTrue(resultado.contains("14"));
        verify(resenaRepository, times(1)).deleteAll();
    }
}
