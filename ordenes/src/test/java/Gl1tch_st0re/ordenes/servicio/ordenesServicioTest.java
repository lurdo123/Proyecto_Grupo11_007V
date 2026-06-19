package Gl1tch_st0re.ordenes.servicio;

import Gl1tch_st0re.ordenes.client.catalogoWebClient;
import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.ordenes.exceptions.ordenesNotFoundException;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.repositorio.ordenesRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de órdenes.
 *
 * Se usa Mockito puro (sin levantar contexto de Spring) para mantener las
 * pruebas rápidas y realmente "unitarias": se mockean el repositorio JPA
 * y el WebClient hacia el microservicio catálogo, y se valida exclusivamente
 * la lógica de negocio contenida en ordenesServicio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ordenesServicio - pruebas unitarias")
class ordenesServicioTest {

    @Mock
    private ordenesRepositorio ordenesRepositorio;

    @Mock
    private catalogoWebClient catalogoWebClient;

    @InjectMocks
    private ordenesServicio ordenesServicio;

    private ordenesRequestDTO dtoValido;
    private catalogoClienteDTO productoDisponible;

    @BeforeEach
    void setUp() {
        dtoValido = new ordenesRequestDTO();
        dtoValido.setUsuario("juan.perez");
        dtoValido.setProductoId(10L);
        dtoValido.setCantidad(2);
        dtoValido.setEstado("pendiente");

        productoDisponible = new catalogoClienteDTO();
        productoDisponible.setId(10L);
        productoDisponible.setNombre("Tarjeta de video RTX 4070");
        productoDisponible.setPrecio(new BigDecimal("450000"));
        productoDisponible.setStock(5);
        productoDisponible.setDisponible(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todas las órdenes registradas")
    void findAll_retornaListaCompleta() {
        ordenesModelo orden1 = ordenesModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        ordenesModelo orden2 = ordenesModelo.builder().id(2L).usuario("luis").estado("ENVIADA").build();
        when(ordenesRepositorio.findAll()).thenReturn(List.of(orden1, orden2));

        List<ordenesModelo> resultado = ordenesServicio.findAll();

        assertEquals(2, resultado.size());
        verify(ordenesRepositorio, times(1)).findAll();
    }

    // ---------- findById ----------

    @Test
    @DisplayName("findById retorna la orden cuando existe")
    void findById_existente_retornaOrden() {
        ordenesModelo orden = ordenesModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(ordenesRepositorio.findById(1L)).thenReturn(Optional.of(orden));

        ordenesModelo resultado = ordenesServicio.findById(1L);

        assertNotNull(resultado);
        assertEquals("ana", resultado.getUsuario());
    }

    @Test
    @DisplayName("findById lanza ordenesNotFoundException cuando no existe")
    void findById_inexistente_lanzaExcepcion() {
        when(ordenesRepositorio.findById(99L)).thenReturn(Optional.empty());

        ordenesNotFoundException ex = assertThrows(ordenesNotFoundException.class,
                () -> ordenesServicio.findById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- findByUsuario ----------

    @Test
    @DisplayName("findByUsuario retorna las órdenes del usuario cuando existen")
    void findByUsuario_conResultados_retornaLista() {
        ordenesModelo orden = ordenesModelo.builder().id(1L).usuario("juan.perez").estado("PENDIENTE").build();
        when(ordenesRepositorio.findByUsuario("juan.perez")).thenReturn(List.of(orden));

        List<ordenesModelo> resultado = ordenesServicio.findByUsuario("juan.perez");

        assertEquals(1, resultado.size());
        assertEquals("juan.perez", resultado.get(0).getUsuario());
    }

    @Test
    @DisplayName("findByUsuario lanza excepción cuando el usuario no tiene órdenes")
    void findByUsuario_sinResultados_lanzaExcepcion() {
        when(ordenesRepositorio.findByUsuario("sin.ordenes")).thenReturn(List.of());

        assertThrows(ordenesNotFoundException.class,
                () -> ordenesServicio.findByUsuario("sin.ordenes"));
    }

    // ---------- findByEstado ----------

    @Test
    @DisplayName("findByEstado normaliza el estado a mayúsculas antes de consultar")
    void findByEstado_normalizaAMayusculas() {
        ordenesModelo orden = ordenesModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(ordenesRepositorio.findByEstado("PENDIENTE")).thenReturn(List.of(orden));

        List<ordenesModelo> resultado = ordenesServicio.findByEstado("pendiente");

        assertEquals(1, resultado.size());
        verify(ordenesRepositorio).findByEstado("PENDIENTE");
    }

    @Test
    @DisplayName("findByEstado lanza excepción cuando no hay órdenes con ese estado")
    void findByEstado_sinResultados_lanzaExcepcion() {
        when(ordenesRepositorio.findByEstado("CANCELADA")).thenReturn(List.of());

        assertThrows(ordenesNotFoundException.class,
                () -> ordenesServicio.findByEstado("cancelada"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea la orden cuando el producto existe, está disponible y hay stock suficiente")
        void crear_casoExitoso() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoDisponible);
            when(ordenesRepositorio.save(any(ordenesModelo.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            ordenesModelo resultado = ordenesServicio.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals("PENDIENTE", resultado.getEstado(), "El estado debe normalizarse a mayúsculas");
            assertEquals("Tarjeta de video RTX 4070", resultado.getProducto());
            assertEquals("juan.perez", resultado.getUsuario());
            verify(ordenesRepositorio, times(1)).save(any(ordenesModelo.class));
        }

        @Test
        @DisplayName("lanza excepción cuando el producto no existe en catálogo (falla el WebClient)")
        void crear_productoNoEncontrado_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde catálogo"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> ordenesServicio.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrado en catálogo"));
            verify(ordenesRepositorio, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el producto no está disponible")
        void crear_productoNoDisponible_lanzaExcepcion() {
            productoDisponible.setDisponible(false);
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoDisponible);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> ordenesServicio.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no está disponible"));
            verify(ordenesRepositorio, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el stock es insuficiente para la cantidad solicitada")
        void crear_stockInsuficiente_lanzaExcepcion() {
            productoDisponible.setStock(1);
            dtoValido.setCantidad(5);
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoDisponible);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> ordenesServicio.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("Stock insuficiente"));
            verify(ordenesRepositorio, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar modifica y normaliza el estado de una orden existente")
    void actualizar_casoExitoso() {
        ordenesModelo existente = ordenesModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(ordenesRepositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(ordenesRepositorio.save(any(ordenesModelo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        ordenesRequestDTO dtoActualizacion = new ordenesRequestDTO();
        dtoActualizacion.setUsuario("ana.maria");
        dtoActualizacion.setProductoId(20L);
        dtoActualizacion.setCantidad(3);
        dtoActualizacion.setEstado("enviada");

        ordenesModelo resultado = ordenesServicio.actualizar(1L, dtoActualizacion);

        assertEquals("ana.maria", resultado.getUsuario());
        assertEquals("ENVIADA", resultado.getEstado());
        verify(ordenesRepositorio).save(existente);
    }

    @Test
    @DisplayName("actualizar lanza excepción si la orden a modificar no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(ordenesRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ordenesNotFoundException.class,
                () -> ordenesServicio.actualizar(99L, dtoValido));
        verify(ordenesRepositorio, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra la orden existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        ordenesModelo existente = ordenesModelo.builder()
                .id(1L).usuario("ana").producto("Mouse Gamer").estado("PENDIENTE").build();
        when(ordenesRepositorio.findById(1L)).thenReturn(Optional.of(existente));

        String resultado = ordenesServicio.eliminar(1L);

        assertTrue(resultado.contains("1"));
        assertTrue(resultado.contains("ana"));
        verify(ordenesRepositorio, times(1)).delete(existente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si la orden no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(ordenesRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ordenesNotFoundException.class, () -> ordenesServicio.eliminar(99L));
        verify(ordenesRepositorio, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los registros y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(ordenesRepositorio.count()).thenReturn(5L);

        String resultado = ordenesServicio.eliminarTodos();

        assertTrue(resultado.contains("5"));
        verify(ordenesRepositorio, times(1)).deleteAll();
    }
}