package Gl1tch_st0re.envios.servicio;

import Gl1tch_st0re.envios.client.ordenesWebClient;
import Gl1tch_st0re.envios.dto.request.enviosRequestDTO;
import Gl1tch_st0re.envios.dto.response.ordenClienteDTO;
import Gl1tch_st0re.envios.exceptions.enviosNotFoundException;
import Gl1tch_st0re.envios.modelo.enviosModelo;
import Gl1tch_st0re.envios.repositorio.enviosRepositorio;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de envíos.
 *
 * Se mockean el repositorio JPA y el WebClient hacia el microservicio
 * ordenes para aislar y validar exclusivamente la lógica de negocio
 * contenida en enviosServicio (sin levantar contexto de Spring ni base
 * de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("enviosServicio - pruebas unitarias")
class enviosServicioTest {

    @Mock
    private enviosRepositorio enviosRepositorio;

    @Mock
    private ordenesWebClient ordenesWebClient;

    @InjectMocks
    private enviosServicio enviosServicio;

    private enviosRequestDTO dtoValido;
    private ordenClienteDTO ordenValida;

    @BeforeEach
    void setUp() {
        dtoValido = new enviosRequestDTO();
        dtoValido.setOrdenId(10L);
        dtoValido.setUsuario("juan.perez");
        dtoValido.setDireccion("Av. Siempre Viva 742");
        dtoValido.setEstado("pendiente");
        dtoValido.setTransportista("Chilexpress");
        dtoValido.setFechaEntregaEstimada(LocalDateTime.now().plusDays(3));

        ordenValida = new ordenClienteDTO();
        ordenValida.setId(10L);
        ordenValida.setUsuario("juan.perez");
        ordenValida.setProducto("Tarjeta de video RTX 4070");
        ordenValida.setEstado("PENDIENTE");
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los envíos registrados")
    void findAll_retornaListaCompleta() {
        enviosModelo envio = enviosModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(enviosRepositorio.findAll()).thenReturn(List.of(envio));

        List<enviosModelo> resultado = enviosServicio.findAll();

        assertEquals(1, resultado.size());
        verify(enviosRepositorio, times(1)).findAll();
    }

    // ---------- findById ----------

    @Test
    @DisplayName("findById retorna el envío cuando existe")
    void findById_existente_retornaEnvio() {
        enviosModelo envio = enviosModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(enviosRepositorio.findById(1L)).thenReturn(Optional.of(envio));

        enviosModelo resultado = enviosServicio.findById(1L);

        assertNotNull(resultado);
        assertEquals("ana", resultado.getUsuario());
    }

    @Test
    @DisplayName("findById lanza enviosNotFoundException cuando no existe")
    void findById_inexistente_lanzaExcepcion() {
        when(enviosRepositorio.findById(99L)).thenReturn(Optional.empty());

        enviosNotFoundException ex = assertThrows(enviosNotFoundException.class,
                () -> enviosServicio.findById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- findByUsuario ----------

    @Test
    @DisplayName("findByUsuario retorna los envíos del usuario cuando existen")
    void findByUsuario_conResultados_retornaLista() {
        enviosModelo envio = enviosModelo.builder().id(1L).usuario("juan.perez").estado("PENDIENTE").build();
        when(enviosRepositorio.findByUsuario("juan.perez")).thenReturn(List.of(envio));

        List<enviosModelo> resultado = enviosServicio.findByUsuario("juan.perez");

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("findByUsuario lanza excepción cuando el usuario no tiene envíos")
    void findByUsuario_sinResultados_lanzaExcepcion() {
        when(enviosRepositorio.findByUsuario("sin.envios")).thenReturn(List.of());

        assertThrows(enviosNotFoundException.class,
                () -> enviosServicio.findByUsuario("sin.envios"));
    }

    // ---------- findByEstado ----------

    @Test
    @DisplayName("findByEstado normaliza el estado a mayúsculas antes de consultar")
    void findByEstado_normalizaAMayusculas() {
        enviosModelo envio = enviosModelo.builder().id(1L).usuario("ana").estado("EN_TRANSITO").build();
        when(enviosRepositorio.findByEstado("EN_TRANSITO")).thenReturn(List.of(envio));

        List<enviosModelo> resultado = enviosServicio.findByEstado("en_transito");

        assertEquals(1, resultado.size());
        verify(enviosRepositorio).findByEstado("EN_TRANSITO");
    }

    @Test
    @DisplayName("findByEstado lanza excepción cuando no hay envíos con ese estado")
    void findByEstado_sinResultados_lanzaExcepcion() {
        when(enviosRepositorio.findByEstado("CANCELADO")).thenReturn(List.of());

        assertThrows(enviosNotFoundException.class,
                () -> enviosServicio.findByEstado("cancelado"));
    }

    // ---------- findByOrdenId ----------

    @Test
    @DisplayName("findByOrdenId retorna los envíos asociados a la orden cuando existen")
    void findByOrdenId_conResultados_retornaLista() {
        enviosModelo envio = enviosModelo.builder().id(1L).ordenId(10L).usuario("ana").build();
        when(enviosRepositorio.findByOrdenId(10L)).thenReturn(List.of(envio));

        List<enviosModelo> resultado = enviosServicio.findByOrdenId(10L);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("findByOrdenId lanza excepción cuando la orden no tiene envíos asociados")
    void findByOrdenId_sinResultados_lanzaExcepcion() {
        when(enviosRepositorio.findByOrdenId(50L)).thenReturn(List.of());

        assertThrows(enviosNotFoundException.class,
                () -> enviosServicio.findByOrdenId(50L));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea el envío cuando la orden existe en el servicio ordenes")
        void crear_casoExitoso() {
            when(ordenesWebClient.obtenerOrden(10L, "Bearer token123")).thenReturn(ordenValida);
            when(enviosRepositorio.save(any(enviosModelo.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            enviosModelo resultado = enviosServicio.crear(dtoValido, "Bearer token123");

            assertNotNull(resultado);
            assertEquals("PENDIENTE", resultado.getEstado(), "El estado debe normalizarse a mayúsculas");
            assertEquals(10L, resultado.getOrdenId());
            assertEquals("Chilexpress", resultado.getTransportista());
            verify(enviosRepositorio, times(1)).save(any(enviosModelo.class));
        }

        @Test
        @DisplayName("lanza excepción cuando la orden no existe en el servicio ordenes (falla el WebClient)")
        void crear_ordenNoEncontrada_lanzaExcepcion() {
            when(ordenesWebClient.obtenerOrden(anyLong(), anyString()))
                    .thenThrow(new RuntimeException("404 desde ordenes"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> enviosServicio.crear(dtoValido, "Bearer token123"));
            assertTrue(ex.getMessage().contains("no encontrada"));
            verify(enviosRepositorio, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el WebClient retorna una orden nula")
        void crear_ordenNula_lanzaExcepcion() {
            when(ordenesWebClient.obtenerOrden(10L, "Bearer token123")).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> enviosServicio.crear(dtoValido, "Bearer token123"));
            verify(enviosRepositorio, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar modifica y normaliza el estado de un envío existente")
    void actualizar_casoExitoso() {
        enviosModelo existente = enviosModelo.builder().id(1L).usuario("ana").estado("PENDIENTE").build();
        when(enviosRepositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(enviosRepositorio.save(any(enviosModelo.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        enviosRequestDTO dtoActualizacion = new enviosRequestDTO();
        dtoActualizacion.setOrdenId(20L);
        dtoActualizacion.setUsuario("ana.maria");
        dtoActualizacion.setDireccion("Calle Falsa 123");
        dtoActualizacion.setEstado("entregado");
        dtoActualizacion.setTransportista("Correos de Chile");
        dtoActualizacion.setFechaEntregaEstimada(LocalDateTime.now());

        enviosModelo resultado = enviosServicio.actualizar(1L, dtoActualizacion);

        assertEquals("ana.maria", resultado.getUsuario());
        assertEquals("ENTREGADO", resultado.getEstado());
        verify(enviosRepositorio).save(existente);
    }

    @Test
    @DisplayName("actualizar lanza excepción si el envío a modificar no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(enviosRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(enviosNotFoundException.class,
                () -> enviosServicio.actualizar(99L, dtoValido));
        verify(enviosRepositorio, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el envío existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        enviosModelo existente = enviosModelo.builder()
                .id(1L).usuario("ana").direccion("Av. Siempre Viva 742").estado("PENDIENTE").build();
        when(enviosRepositorio.findById(1L)).thenReturn(Optional.of(existente));

        String resultado = enviosServicio.eliminar(1L);

        assertTrue(resultado.contains("ana"));
        assertTrue(resultado.contains("Av. Siempre Viva 742"));
        verify(enviosRepositorio, times(1)).delete(existente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el envío no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(enviosRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(enviosNotFoundException.class, () -> enviosServicio.eliminar(99L));
        verify(enviosRepositorio, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los registros y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(enviosRepositorio.count()).thenReturn(6L);

        String resultado = enviosServicio.eliminarTodos();

        assertTrue(resultado.contains("6"));
        verify(enviosRepositorio, times(1)).deleteAll();
    }
}