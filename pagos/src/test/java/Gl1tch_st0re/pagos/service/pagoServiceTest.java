package Gl1tch_st0re.pagos.service;

import Gl1tch_st0re.pagos.client.ordenesWebClient;
import Gl1tch_st0re.pagos.dto.request.pagoRequestDTO;
import Gl1tch_st0re.pagos.dto.response.ordenClienteDTO;
import Gl1tch_st0re.pagos.exceptions.pagoNotFoundException;
import Gl1tch_st0re.pagos.model.pagoModel;
import Gl1tch_st0re.pagos.repository.pagoRepository;

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
 * Pruebas unitarias del servicio de pagos.
 *
 * Se mockean el repositorio JPA y el WebClient hacia el microservicio
 * ordenes para aislar y validar exclusivamente la lógica de negocio
 * contenida en pagoService: validación cruzada de la orden, unicidad
 * de idTransaccionExterna y manejo de pagos inexistentes (sin levantar
 * contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("pagoService - pruebas unitarias")
class pagoServiceTest {

    @Mock
    private pagoRepository pagoRepository;

    @Mock
    private ordenesWebClient ordenesWebClient;

    @InjectMocks
    private pagoService pagoService;

    private pagoModel pagoExistente;
    private pagoRequestDTO dtoValido;
    private ordenClienteDTO ordenValida;

    @BeforeEach
    void setUp() {
        pagoExistente = pagoModel.builder()
                .id(1L)
                .ordenId(10L)
                .idTransaccionExterna("TXN-001")
                .metodoPago("Tarjeta de crédito")
                .montoPagado(150000.0)
                .estadoPago("APROBADO")
                .build();

        dtoValido = new pagoRequestDTO();
        dtoValido.setOrdenId(10L);
        dtoValido.setIdTransaccionExterna("TXN-002");
        dtoValido.setMetodoPago("Transferencia bancaria");
        dtoValido.setMontoPagado(75000.0);
        dtoValido.setEstadoPago("PENDIENTE");

        ordenValida = new ordenClienteDTO();
        ordenValida.setId(10L);
        ordenValida.setUsuario("juan.perez");
        ordenValida.setProducto("Tarjeta de video RTX 4070");
        ordenValida.setEstado("PENDIENTE");
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los pagos registrados")
    void findAll_retornaListaCompleta() {
        pagoModel otro = pagoModel.builder().id(2L).ordenId(20L).build();
        when(pagoRepository.findAll()).thenReturn(List.of(pagoExistente, otro));

        List<pagoModel> resultado = pagoService.findAll();

        assertEquals(2, resultado.size());
        verify(pagoRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna el pago cuando existe")
    void obtenerPorId_existente_retornaPago() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));

        pagoModel resultado = pagoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("TXN-001", resultado.getIdTransaccionExterna());
        assertEquals("APROBADO", resultado.getEstadoPago());
    }

    @Test
    @DisplayName("obtenerPorId lanza pagoNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        pagoNotFoundException ex = assertThrows(pagoNotFoundException.class,
                () -> pagoService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea el pago cuando la orden existe y la transacción no está duplicada")
        void crear_casoExitoso() {
            when(ordenesWebClient.obtenerOrden(10L, "Bearer token123")).thenReturn(ordenValida);
            when(pagoRepository.existsByIdTransaccionExterna("TXN-002")).thenReturn(false);
            when(pagoRepository.save(any(pagoModel.class))).thenAnswer(invocacion -> {
                pagoModel modelo = invocacion.getArgument(0);
                modelo.setId(5L);
                return modelo;
            });

            pagoModel resultado = pagoService.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals(10L, resultado.getOrdenId());
            assertEquals("TXN-002", resultado.getIdTransaccionExterna());
            assertEquals("Transferencia bancaria", resultado.getMetodoPago());
            assertEquals(75000.0, resultado.getMontoPagado());
            verify(pagoRepository, times(1)).save(any(pagoModel.class));
        }

        @Test
        @DisplayName("lanza excepción cuando la orden no existe en el servicio ordenes (falla el WebClient)")
        void crear_ordenNoEncontrada_lanzaExcepcion() {
            when(ordenesWebClient.obtenerOrden(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde ordenes"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pagoService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrada"));
            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el WebClient retorna una orden nula")
        void crear_ordenNula_lanzaExcepcion() {
            when(ordenesWebClient.obtenerOrden(10L, "Bearer token123")).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> pagoService.crear(dtoValido, "Bearer token123"));
            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando ya existe un pago con la misma transacción externa")
        void crear_transaccionDuplicada_lanzaExcepcion() {
            when(ordenesWebClient.obtenerOrden(10L, "Bearer token123")).thenReturn(ordenValida);
            when(pagoRepository.existsByIdTransaccionExterna("TXN-002")).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pagoService.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("Ya existe un pago con la transaccion"));
            verify(pagoRepository, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza el pago cuando la transacción no pertenece a otro registro")
        void actualizar_casoExitoso() {
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));
            when(pagoRepository.existsByIdTransaccionExternaAndIdNot("TXN-002", 1L)).thenReturn(false);
            when(pagoRepository.save(any(pagoModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            pagoModel resultado = pagoService.actualizar(1L, dtoValido);

            assertEquals(10L, resultado.getOrdenId());
            assertEquals("TXN-002", resultado.getIdTransaccionExterna());
            assertEquals("Transferencia bancaria", resultado.getMetodoPago());
            assertEquals(75000.0, resultado.getMontoPagado());
            assertEquals("PENDIENTE", resultado.getEstadoPago());
            verify(pagoRepository, times(1)).save(pagoExistente);
        }

        @Test
        @DisplayName("lanza excepción cuando la transacción ya pertenece a otro pago")
        void actualizar_transaccionDuplicadaEnOtroPago_lanzaExcepcion() {
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));
            when(pagoRepository.existsByIdTransaccionExternaAndIdNot("TXN-002", 1L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> pagoService.actualizar(1L, dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe otro pago con la transaccion"));
            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el pago a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(pagoNotFoundException.class,
                    () -> pagoService.actualizar(99L, dtoValido));
            verify(pagoRepository, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el pago existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));

        String resultado = pagoService.eliminar(1L);

        assertTrue(resultado.contains("1"));
        assertTrue(resultado.contains("10"));
        assertTrue(resultado.contains("TXN-001"));
        verify(pagoRepository, times(1)).delete(pagoExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el pago no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(pagoNotFoundException.class, () -> pagoService.eliminar(99L));
        verify(pagoRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los pagos y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(pagoRepository.count()).thenReturn(3L);

        String resultado = pagoService.eliminarTodos();

        assertTrue(resultado.contains("3"));
        verify(pagoRepository, times(1)).deleteAll();
    }
}
