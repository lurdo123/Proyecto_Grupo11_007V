package Gl1tch_st0re.compatibilidad.servicio;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.exceptions.compatibilidadNotFoundException;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.repositorio.compatibilidadRepositorio;

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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de compatibilidad.
 *
 * Se mockea el repositorio JPA para aislar y validar exclusivamente
 * la lógica de negocio contenida en compatibilidadServicio: unicidad
 * de la pareja componenteBase/componenteCompatible, exclusión del
 * propio registro al actualizar y la verificación pública de
 * compatibilidad (sin levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("compatibilidadServicio - pruebas unitarias")
class compatibilidadServicioTest {

    @Mock
    private compatibilidadRepositorio compatibilidadRepositorio;

    @InjectMocks
    private compatibilidadServicio compatibilidadServicio;

    private compatibilidadModelo registroExistente;
    private compatibilidadRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        registroExistente = compatibilidadModelo.builder()
                .id(1L)
                .componenteBase("Placa Madre ASUS B550")
                .componenteCompatible("Ryzen 5 5600X")
                .tipo("Socket AM4")
                .build();

        dtoValido = new compatibilidadRequestDTO();
        dtoValido.setComponenteBase("Placa Madre MSI Z690");
        dtoValido.setComponenteCompatible("Intel i5-12600K");
        dtoValido.setTipo("Socket LGA1700");
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los registros de compatibilidad")
    void findAll_retornaListaCompleta() {
        when(compatibilidadRepositorio.findAll()).thenReturn(List.of(registroExistente));

        List<compatibilidadModelo> resultado = compatibilidadServicio.findAll();

        assertEquals(1, resultado.size());
        verify(compatibilidadRepositorio, times(1)).findAll();
    }

    // ---------- findById ----------

    @Test
    @DisplayName("findById retorna el registro cuando existe")
    void findById_existente_retornaRegistro() {
        when(compatibilidadRepositorio.findById(1L)).thenReturn(Optional.of(registroExistente));

        compatibilidadModelo resultado = compatibilidadServicio.findById(1L);

        assertNotNull(resultado);
        assertEquals("Ryzen 5 5600X", resultado.getComponenteCompatible());
    }

    @Test
    @DisplayName("findById lanza compatibilidadNotFoundException cuando no existe")
    void findById_inexistente_lanzaExcepcion() {
        when(compatibilidadRepositorio.findById(99L)).thenReturn(Optional.empty());

        compatibilidadNotFoundException ex = assertThrows(compatibilidadNotFoundException.class,
                () -> compatibilidadServicio.findById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea la compatibilidad cuando la pareja de componentes no existe")
        void crear_casoExitoso() {
            when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                    "Placa Madre MSI Z690", "Intel i5-12600K")).thenReturn(false);
            when(compatibilidadRepositorio.save(any(compatibilidadModelo.class))).thenAnswer(invocacion -> {
                compatibilidadModelo modelo = invocacion.getArgument(0);
                modelo.setId(5L);
                return modelo;
            });

            compatibilidadModelo resultado = compatibilidadServicio.crear(dtoValido);

            assertNotNull(resultado);
            assertEquals("Placa Madre MSI Z690", resultado.getComponenteBase());
            assertEquals("Socket LGA1700", resultado.getTipo());
            verify(compatibilidadRepositorio, times(1)).save(any(compatibilidadModelo.class));
        }

        @Test
        @DisplayName("lanza excepción cuando ya existe compatibilidad entre esos componentes")
        void crear_parejaDuplicada_lanzaExcepcion() {
            when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                    "Placa Madre MSI Z690", "Intel i5-12600K")).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> compatibilidadServicio.crear(dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe una compatibilidad"));
            verify(compatibilidadRepositorio, never()).save(any());
        }
    }

    // ---------- actualizar ----------

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza el registro cuando la nueva pareja no pertenece a otro registro")
        void actualizar_casoExitoso() {
            when(compatibilidadRepositorio.findById(1L)).thenReturn(Optional.of(registroExistente));
            when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatibleAndIdNot(
                    "Placa Madre MSI Z690", "Intel i5-12600K", 1L)).thenReturn(false);
            when(compatibilidadRepositorio.save(any(compatibilidadModelo.class)))
                    .thenAnswer(invocacion -> invocacion.getArgument(0));

            compatibilidadModelo resultado = compatibilidadServicio.actualizar(1L, dtoValido);

            assertEquals("Placa Madre MSI Z690", resultado.getComponenteBase());
            assertEquals("Intel i5-12600K", resultado.getComponenteCompatible());
            verify(compatibilidadRepositorio, times(1)).save(registroExistente);
        }

        @Test
        @DisplayName("lanza excepción cuando la nueva pareja ya pertenece a otro registro")
        void actualizar_parejaDuplicadaEnOtroRegistro_lanzaExcepcion() {
            when(compatibilidadRepositorio.findById(1L)).thenReturn(Optional.of(registroExistente));
            when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatibleAndIdNot(
                    "Placa Madre MSI Z690", "Intel i5-12600K", 1L)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> compatibilidadServicio.actualizar(1L, dtoValido));
            assertTrue(ex.getMessage().contains("Ya existe otra compatibilidad"));
            verify(compatibilidadRepositorio, never()).save(any());
        }

        @Test
        @DisplayName("lanza excepción cuando el registro a actualizar no existe")
        void actualizar_inexistente_lanzaExcepcion() {
            when(compatibilidadRepositorio.findById(99L)).thenReturn(Optional.empty());

            assertThrows(compatibilidadNotFoundException.class,
                    () -> compatibilidadServicio.actualizar(99L, dtoValido));
            verify(compatibilidadRepositorio, never()).save(any());
        }
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el registro existente y retorna un mensaje descriptivo")
    void eliminar_casoExitoso() {
        when(compatibilidadRepositorio.findById(1L)).thenReturn(Optional.of(registroExistente));

        String resultado = compatibilidadServicio.eliminar(1L);

        assertTrue(resultado.contains("Placa Madre ASUS B550"));
        assertTrue(resultado.contains("Ryzen 5 5600X"));
        verify(compatibilidadRepositorio, times(1)).delete(registroExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el registro no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(compatibilidadRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(compatibilidadNotFoundException.class,
                () -> compatibilidadServicio.eliminar(99L));
        verify(compatibilidadRepositorio, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los registros y retorna el total eliminado")
    void eliminarTodos_casoExitoso() {
        when(compatibilidadRepositorio.count()).thenReturn(4L);

        String resultado = compatibilidadServicio.eliminarTodos();

        assertTrue(resultado.contains("4"));
        verify(compatibilidadRepositorio, times(1)).deleteAll();
    }

    // ---------- verificarCompatibilidad ----------

    @Test
    @DisplayName("verificarCompatibilidad retorna true cuando la pareja de componentes es compatible")
    void verificarCompatibilidad_compatible_retornaTrue() {
        when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                "Placa Madre ASUS B550", "Ryzen 5 5600X")).thenReturn(true);

        boolean resultado = compatibilidadServicio.verificarCompatibilidad(
                "Placa Madre ASUS B550", "Ryzen 5 5600X");

        assertTrue(resultado);
    }

    @Test
    @DisplayName("verificarCompatibilidad retorna false cuando la pareja de componentes no es compatible")
    void verificarCompatibilidad_noCompatible_retornaFalse() {
        when(compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                "Placa Madre ASUS B550", "GPU RTX 4090")).thenReturn(false);

        boolean resultado = compatibilidadServicio.verificarCompatibilidad(
                "Placa Madre ASUS B550", "GPU RTX 4090");

        assertFalse(resultado);
    }
}