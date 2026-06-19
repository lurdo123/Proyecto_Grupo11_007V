package Gl1tch_st0re.clientes.service;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.exceptions.clienteNotFoundException;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.repository.clienteRepository;

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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de clientes.
 *
 * Se mockea el repositorio JPA para aislar y validar exclusivamente
 * la lógica de negocio contenida en clienteService: unicidad de
 * usuario_id, valor por defecto del nivel de fidelidad y manejo de
 * clientes inexistentes (sin levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("clienteService - pruebas unitarias")
class clienteServiceTest {

    @Mock
    private clienteRepository clienteRepository;

    @InjectMocks
    private clienteService clienteService;

    private clienteModel clienteExistente;
    private clienteRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        clienteExistente = clienteModel.builder()
                .id(1L)
                .usuarioId(100L)
                .nombre("Carlos")
                .apellido("Soto")
                .telefono("+56912345678")
                .nivelFidelidad("Plata")
                .totalCompradoHistorico(150000.0)
                .build();

        dtoValido = new clienteRequestDTO();
        dtoValido.setUsuarioId(200L);
        dtoValido.setNombre("Valentina");
        dtoValido.setApellido("Rojas");
        dtoValido.setTelefono("+56987654321");
        dtoValido.setNivelFidelidad("Oro");
        dtoValido.setTotalCompradoHistorico(50000.0);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los clientes registrados")
    void findAll_retornaListaCompleta() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteExistente));

        List<clienteModel> resultado = clienteService.findAll();

        assertEquals(1, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna el cliente cuando existe")
    void obtenerPorId_existente_retornaCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));

        clienteModel resultado = clienteService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId lanza clienteNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        clienteNotFoundException ex = assertThrows(clienteNotFoundException.class,
                () -> clienteService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea el cliente cuando el usuario_id no tiene perfil previo")
        void crear_casoExitoso() {
            when(clienteRepository.existsByUsuarioId(200L)).thenReturn(false);
            when(clienteRepository.save(any(clienteModel.class))).thenAnswer(invocacion -> {
                clienteModel modelo = invocacion.getArgument(0);
                modelo.setId(10L);
                return modelo;
            });

            clienteModel resultado = clienteService.crear(dtoValido);

            assertNotNull(resultado);
            assertEquals(200L, resultado.getUsuarioId());
            assertEquals("Valentina", resultado.getNombre());
            assertEquals("Oro", resultado.getNivelFidelidad());
            verify(clienteRepository, times(1)).save(any(clienteModel.class));
        }

        @Test
        @DisplayName("asigna 'Bronce' como nivel de fidelidad cuando no se especifica")
        void crear_sinNivelFidelidad_asignaBronce() {
            dtoValido.setNivelFidelidad(null);
            when(clienteRepository.existsByUsuarioId(200L)).thenReturn(false);
            when(clienteRepository.save(any(clienteModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            clienteModel resultado = clienteService.crear(dtoValido);

            assertEquals("Bronce", resultado.getNivelFidelidad());
        }

        @Test
        @DisplayName("lanza excepción cuando ya existe un perfil para el usuario_id")
        void crear_usuarioIdDuplicado_lanzaExcepcion() {
            when(clienteRepository.existsByUsuarioId(200L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> clienteService.crear(dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe un perfil"));
            verify(clienteRepository, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza el cliente cuando el usuario_id no pertenece a otro perfil")
        void actualizar_casoExitoso() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.existsByUsuarioIdAndIdNot(200L, 1L)).thenReturn(false);
            when(clienteRepository.save(any(clienteModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            clienteModel resultado = clienteService.actualizar(1L, dtoValido);

            assertEquals("Valentina", resultado.getNombre());
            assertEquals(200L, resultado.getUsuarioId());
            verify(clienteRepository, times(1)).save(clienteExistente);
        }

        @Test
        @DisplayName("asigna 'Bronce' como nivel de fidelidad cuando no se especifica al actualizar")
        void actualizar_sinNivelFidelidad_asignaBronce() {
            dtoValido.setNivelFidelidad(null);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.existsByUsuarioIdAndIdNot(200L, 1L)).thenReturn(false);
            when(clienteRepository.save(any(clienteModel.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            clienteModel resultado = clienteService.actualizar(1L, dtoValido);

            assertEquals("Bronce", resultado.getNivelFidelidad());
        }

        @Test
        @DisplayName("lanza excepción cuando el usuario_id ya pertenece a otro cliente")
        void actualizar_usuarioIdDuplicado_lanzaExcepcion() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.existsByUsuarioIdAndIdNot(200L, 1L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> clienteService.actualizar(1L, dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe otro perfil"));
            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el cliente a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(clienteNotFoundException.class,
                    () -> clienteService.actualizar(99L, dtoValido));
            verify(clienteRepository, never()).existsByUsuarioIdAndIdNot(anyLong(), anyLong());
            verify(clienteRepository, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el cliente existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));

        String resultado = clienteService.eliminar(1L);

        assertTrue(resultado.contains("100"));
        assertTrue(resultado.contains("Carlos"));
        verify(clienteRepository, times(1)).delete(clienteExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el cliente no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(clienteNotFoundException.class, () -> clienteService.eliminar(99L));
        verify(clienteRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los registros y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(clienteRepository.count()).thenReturn(9L);

        String resultado = clienteService.eliminarTodos();

        assertTrue(resultado.contains("9"));
        verify(clienteRepository, times(1)).deleteAll();
    }
}