package Gl1tch_st0re.inventario.service;

import Gl1tch_st0re.inventario.client.catalogoWebClient;
import Gl1tch_st0re.inventario.dto.request.inventarioRequestDTO;
import Gl1tch_st0re.inventario.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.inventario.exceptions.inventarioNotFoundException;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.repository.inventarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de inventario.
 *
 * Se mockean el repositorio JPA y el WebClient hacia el microservicio
 * catalogo para aislar y validar exclusivamente la lógica de negocio
 * contenida en inventarioService: validación cruzada del producto y
 * unicidad de productoId, tanto al crear como al actualizar (sin
 * levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("inventarioService - pruebas unitarias")
class inventarioServiceTest {

    @Mock
    private inventarioRepository inventarioRepository;

    @Mock
    private catalogoWebClient catalogoWebClient;

    @InjectMocks
    private inventarioService inventarioService;

    private inventarioRequestDTO dtoValido;
    private catalogoClienteDTO productoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new inventarioRequestDTO();
        dtoValido.setProductoId(10L);
        dtoValido.setEstadoFisico("Nuevo");
        dtoValido.setCantidadDisponible(25);
        dtoValido.setUbicacionBodega("Bodega Central - Pasillo 4");

        productoValido = new catalogoClienteDTO();
        productoValido.setId(10L);
        productoValido.setNombre("Tarjeta de video RTX 4070");
        productoValido.setDisponible(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los registros de inventario")
    void findAll_retornaListaCompleta() {
        inventarioModel inv = inventarioModel.builder().id(1L).productoId(10L).build();
        when(inventarioRepository.findAll()).thenReturn(List.of(inv));

        List<inventarioModel> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna el registro cuando existe")
    void obtenerPorId_existente_retornaRegistro() {
        inventarioModel inv = inventarioModel.builder().id(1L).productoId(10L).estadoFisico("Nuevo").build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));

        inventarioModel resultado = inventarioService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Nuevo", resultado.getEstadoFisico());
    }

    @Test
    @DisplayName("obtenerPorId lanza inventarioNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        inventarioNotFoundException ex = assertThrows(inventarioNotFoundException.class,
                () -> inventarioService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea el registro cuando el producto existe en catálogo y no tiene inventario previo")
        void crear_casoExitoso() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoValido);
            when(inventarioRepository.existsByProductoId(10L)).thenReturn(false);
            when(inventarioRepository.save(any(inventarioModel.class))).thenAnswer(invocacion -> {
                inventarioModel modelo = invocacion.getArgument(0);
                modelo.setId(1L);
                return modelo;
            });

            inventarioModel resultado = inventarioService.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals(10L, resultado.getProductoId());
            assertEquals(25, resultado.getCantidadDisponible());
            verify(inventarioRepository, times(1)).save(any(inventarioModel.class));
        }

        @Test
        @DisplayName("lanza excepción cuando el producto no existe en catálogo (falla el WebClient)")
        void crear_productoNoEncontrado_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde catálogo"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> inventarioService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrado en catálogo"));
            verify(inventarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el WebClient retorna un producto nulo")
        void crear_productoNulo_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> inventarioService.crear(dtoValido, "Bearer token123"));
            verify(inventarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando ya existe inventario para ese producto")
        void crear_productoDuplicado_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoValido);
            when(inventarioRepository.existsByProductoId(10L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> inventarioService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("Ya existe un inventario"));
            verify(inventarioRepository, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza el registro cuando el nuevo productoId no pertenece a otro inventario")
        void actualizar_casoExitoso() {
            inventarioModel existente = inventarioModel.builder()
                    .id(1L).productoId(10L).estadoFisico("Nuevo").cantidadDisponible(25)
                    .ubicacionBodega("Bodega Central - Pasillo 4").build();
            when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(inventarioRepository.existsByProductoIdAndIdNot(20L, 1L)).thenReturn(false);
            when(inventarioRepository.save(any(inventarioModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            inventarioRequestDTO dtoActualizacion = new inventarioRequestDTO();
            dtoActualizacion.setProductoId(20L);
            dtoActualizacion.setEstadoFisico("Usado");
            dtoActualizacion.setCantidadDisponible(5);
            dtoActualizacion.setUbicacionBodega("Bodega Norte - Pasillo 1");

            inventarioModel resultado = inventarioService.actualizar(1L, dtoActualizacion);

            assertEquals(20L, resultado.getProductoId());
            assertEquals("Usado", resultado.getEstadoFisico());
            assertEquals(5, resultado.getCantidadDisponible());
            verify(inventarioRepository, times(1)).save(existente);
        }

        @Test
        @DisplayName("lanza excepción cuando el nuevo productoId ya pertenece a otro inventario")
        void actualizar_productoDuplicadoEnOtroRegistro_lanzaExcepcion() {
            inventarioModel existente = inventarioModel.builder().id(1L).productoId(10L).build();
            when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(inventarioRepository.existsByProductoIdAndIdNot(20L, 1L)).thenReturn(true);

            inventarioRequestDTO dtoActualizacion = new inventarioRequestDTO();
            dtoActualizacion.setProductoId(20L);
            dtoActualizacion.setEstadoFisico("Usado");
            dtoActualizacion.setCantidadDisponible(5);
            dtoActualizacion.setUbicacionBodega("Bodega Norte - Pasillo 1");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> inventarioService.actualizar(1L, dtoActualizacion));
            assertTrue(ex.getMessage().contains("Ya existe otro inventario"));
            verify(inventarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el inventario a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(inventarioNotFoundException.class,
                    () -> inventarioService.actualizar(99L, dtoValido));
            verify(inventarioRepository, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el registro existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        inventarioModel existente = inventarioModel.builder()
                .id(1L).productoId(10L).estadoFisico("Nuevo").ubicacionBodega("Bodega Central").build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));

        String resultado = inventarioService.eliminar(1L);

        assertTrue(resultado.contains("10"));
        assertTrue(resultado.contains("Nuevo"));
        verify(inventarioRepository, times(1)).delete(existente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el registro no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(inventarioNotFoundException.class, () -> inventarioService.eliminar(99L));
        verify(inventarioRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los registros y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(inventarioRepository.count()).thenReturn(11L);

        String resultado = inventarioService.eliminarTodos();

        assertTrue(resultado.contains("11"));
        verify(inventarioRepository, times(1)).deleteAll();
    }
}