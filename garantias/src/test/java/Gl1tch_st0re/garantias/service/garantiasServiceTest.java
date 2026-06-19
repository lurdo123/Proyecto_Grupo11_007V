package Gl1tch_st0re.garantias.service;

import Gl1tch_st0re.garantias.client.catalogoWebClient;
import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.garantias.exceptions.garantiasNotFoundException;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.repository.garantiasRepository;

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
 * Pruebas unitarias del servicio de garantías.
 *
 * Se mockean el repositorio JPA y el WebClient hacia el microservicio
 * catalogo para aislar y validar exclusivamente la lógica de negocio
 * contenida en garantiasService (sin levantar contexto de Spring ni
 * base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("garantiasService - pruebas unitarias")
class garantiasServiceTest {

    @Mock
    private garantiasRepository garantiasRepository;

    @Mock
    private catalogoWebClient catalogoWebClient;

    @InjectMocks
    private garantiasService garantiasService;

    private garantiasRequestDTO dtoValido;
    private catalogoClienteDTO productoValido;

    @BeforeEach
    void setUp() {
        dtoValido = new garantiasRequestDTO();
        dtoValido.setProductoId(10L);
        dtoValido.setOrdenId(5L);
        dtoValido.setMesesCobertura(12);
        dtoValido.setFechaVencimiento(LocalDate.now().plusMonths(12));

        productoValido = new catalogoClienteDTO();
        productoValido.setId(10L);
        productoValido.setNombre("Tarjeta de video RTX 4070");
        productoValido.setDisponible(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todas las garantías registradas")
    void findAll_retornaListaCompleta() {
        garantiasModel garantia = garantiasModel.builder().id(1L).productoId(10L).build();
        when(garantiasRepository.findAll()).thenReturn(List.of(garantia));

        List<garantiasModel> resultado = garantiasService.findAll();

        assertEquals(1, resultado.size());
        verify(garantiasRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna la garantía cuando existe")
    void obtenerPorId_existente_retornaGarantia() {
        garantiasModel garantia = garantiasModel.builder().id(1L).productoId(10L).mesesCobertura(12).build();
        when(garantiasRepository.findById(1L)).thenReturn(Optional.of(garantia));

        garantiasModel resultado = garantiasService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(12, resultado.getMesesCobertura());
    }

    @Test
    @DisplayName("obtenerPorId lanza garantiasNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(garantiasRepository.findById(99L)).thenReturn(Optional.empty());

        garantiasNotFoundException ex = assertThrows(garantiasNotFoundException.class,
                () -> garantiasService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea la garantía cuando el producto existe en catálogo")
        void crear_casoExitoso() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(productoValido);
            when(garantiasRepository.save(any(garantiasModel.class))).thenAnswer(invocacion -> {
                garantiasModel modelo = invocacion.getArgument(0);
                modelo.setId(1L);
                return modelo;
            });

            garantiasModel resultado = garantiasService.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals(10L, resultado.getProductoId());
            assertEquals(5L, resultado.getOrdenId());
            assertEquals(12, resultado.getMesesCobertura());
            verify(garantiasRepository, times(1)).save(any(garantiasModel.class));
        }

        @Test
        @DisplayName("lanza excepción cuando el producto no existe en catálogo (falla el WebClient)")
        void crear_productoNoEncontrado_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde catálogo"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> garantiasService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrado en catálogo"));
            verify(garantiasRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el WebClient retorna un producto nulo")
        void crear_productoNulo_lanzaExcepcion() {
            when(catalogoWebClient.obtenerProducto(10L, "Bearer token123")).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> garantiasService.crear(dtoValido, "Bearer token123"));
            verify(garantiasRepository, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar modifica todos los campos de una garantía existente")
    void actualizar_casoExitoso() {
        garantiasModel existente = garantiasModel.builder()
                .id(1L).productoId(10L).ordenId(5L).mesesCobertura(12)
                .fechaVencimiento(LocalDate.now().plusMonths(12)).build();
        when(garantiasRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(garantiasRepository.save(any(garantiasModel.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        garantiasRequestDTO dtoActualizacion = new garantiasRequestDTO();
        dtoActualizacion.setProductoId(20L);
        dtoActualizacion.setOrdenId(8L);
        dtoActualizacion.setMesesCobertura(24);
        dtoActualizacion.setFechaVencimiento(LocalDate.now().plusMonths(24));

        garantiasModel resultado = garantiasService.actualizar(1L, dtoActualizacion);

        assertEquals(20L, resultado.getProductoId());
        assertEquals(8L, resultado.getOrdenId());
        assertEquals(24, resultado.getMesesCobertura());
        verify(garantiasRepository, times(1)).save(existente);
    }

    @Test
    @DisplayName("actualizar lanza excepción si la garantía a modificar no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(garantiasRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(garantiasNotFoundException.class,
                () -> garantiasService.actualizar(99L, dtoValido));
        verify(garantiasRepository, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra la garantía existente")
    void eliminar_casoExitoso() {
        garantiasModel existente = garantiasModel.builder().id(1L).productoId(10L).build();
        when(garantiasRepository.findById(1L)).thenReturn(Optional.of(existente));

        garantiasService.eliminar(1L);

        verify(garantiasRepository, times(1)).delete(existente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si la garantía no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(garantiasRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(garantiasNotFoundException.class, () -> garantiasService.eliminar(99L));
        verify(garantiasRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todas las garantías registradas")
    void eliminarTodos_casoExitoso() {
        when(garantiasRepository.count()).thenReturn(8L);

        garantiasService.eliminarTodos();

        verify(garantiasRepository, times(1)).deleteAll();
    }
}