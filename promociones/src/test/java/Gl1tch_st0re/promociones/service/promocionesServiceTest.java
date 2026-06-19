package Gl1tch_st0re.promociones.service;

import Gl1tch_st0re.promociones.dto.request.promocionesRequestDTO;
import Gl1tch_st0re.promociones.exceptions.promocionesNotFoundException;
import Gl1tch_st0re.promociones.model.promocionesModel;
import Gl1tch_st0re.promociones.repository.promocionesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de promociones.
 *
 * Se mockea el repositorio JPA para aislar y validar exclusivamente
 * la lógica de negocio contenida en promocionesService: creación,
 * actualización y eliminación de promociones con descuento en
 * porcentaje (sin levantar contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("promocionesService - pruebas unitarias")
class promocionesServiceTest {

    @Mock
    private promocionesRepository promocionesRepository;

    @InjectMocks
    private promocionesService promocionesService;

    private promocionesModel promocionExistente;
    private promocionesRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        promocionExistente = promocionesModel.builder()
                .id(1L)
                .codigo("VERANO25")
                .descripcion("Descuento de verano 25%")
                .descuentoPorcentaje(new BigDecimal("25.00"))
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(1))
                .activo(true)
                .build();

        dtoValido = new promocionesRequestDTO();
        dtoValido.setCodigo("INVIERNO15");
        dtoValido.setDescripcion("Descuento de invierno 15%");
        dtoValido.setDescuentoPorcentaje(new BigDecimal("15.00"));
        dtoValido.setFechaInicio(LocalDate.now().plusMonths(2));
        dtoValido.setFechaFin(LocalDate.now().plusMonths(3));
        dtoValido.setActivo(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todas las promociones registradas")
    void findAll_retornaListaCompleta() {
        promocionesModel otra = promocionesModel.builder().id(2L).codigo("OTRO10").build();
        when(promocionesRepository.findAll()).thenReturn(List.of(promocionExistente, otra));

        List<promocionesModel> resultado = promocionesService.findAll();

        assertEquals(2, resultado.size());
        verify(promocionesRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll retorna lista vacía cuando no hay promociones")
    void findAll_sinPromociones_retornaListaVacia() {
        when(promocionesRepository.findAll()).thenReturn(List.of());

        List<promocionesModel> resultado = promocionesService.findAll();

        assertTrue(resultado.isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna la promoción cuando existe")
    void obtenerPorId_existente_retornaPromocion() {
        when(promocionesRepository.findById(1L)).thenReturn(Optional.of(promocionExistente));

        promocionesModel resultado = promocionesService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("VERANO25", resultado.getCodigo());
        assertEquals(new BigDecimal("25.00"), resultado.getDescuentoPorcentaje());
    }

    @Test
    @DisplayName("obtenerPorId lanza promocionesNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(promocionesRepository.findById(99L)).thenReturn(Optional.empty());

        promocionesNotFoundException ex = assertThrows(promocionesNotFoundException.class,
                () -> promocionesService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear guarda la promoción con todos los campos del DTO")
    void crear_casoExitoso() {
        when(promocionesRepository.save(any(promocionesModel.class))).thenAnswer(invocacion -> {
            promocionesModel modelo = invocacion.getArgument(0);
            modelo.setId(10L);
            return modelo;
        });

        promocionesModel resultado = promocionesService.crear(dtoValido);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("INVIERNO15", resultado.getCodigo());
        assertEquals("Descuento de invierno 15%", resultado.getDescripcion());
        assertEquals(new BigDecimal("15.00"), resultado.getDescuentoPorcentaje());
        assertTrue(resultado.getActivo());
        verify(promocionesRepository, times(1)).save(any(promocionesModel.class));
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar modifica todos los campos de la promoción existente")
    void actualizar_casoExitoso() {
        when(promocionesRepository.findById(1L)).thenReturn(Optional.of(promocionExistente));
        when(promocionesRepository.save(any(promocionesModel.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        promocionesRequestDTO dtoActualizacion = new promocionesRequestDTO();
        dtoActualizacion.setCodigo("VERANO25_V2");
        dtoActualizacion.setDescripcion("Descuento especial 30%");
        dtoActualizacion.setDescuentoPorcentaje(new BigDecimal("30.00"));
        dtoActualizacion.setFechaInicio(LocalDate.now().plusDays(5));
        dtoActualizacion.setFechaFin(LocalDate.now().plusMonths(2));
        dtoActualizacion.setActivo(false);

        promocionesModel resultado = promocionesService.actualizar(1L, dtoActualizacion);

        assertEquals("VERANO25_V2", resultado.getCodigo());
        assertEquals("Descuento especial 30%", resultado.getDescripcion());
        assertEquals(new BigDecimal("30.00"), resultado.getDescuentoPorcentaje());
        assertFalse(resultado.getActivo());
        verify(promocionesRepository, times(1)).save(promocionExistente);
    }

    @Test
    @DisplayName("actualizar lanza excepción si la promoción no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(promocionesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(promocionesNotFoundException.class,
                () -> promocionesService.actualizar(99L, dtoValido));
        verify(promocionesRepository, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra la promoción existente")
    void eliminar_casoExitoso() {
        when(promocionesRepository.findById(1L)).thenReturn(Optional.of(promocionExistente));

        promocionesService.eliminar(1L);

        verify(promocionesRepository, times(1)).delete(promocionExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si la promoción no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(promocionesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(promocionesNotFoundException.class,
                () -> promocionesService.eliminar(99L));
        verify(promocionesRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todas las promociones registradas")
    void eliminarTodos_casoExitoso() {
        when(promocionesRepository.count()).thenReturn(5L);

        promocionesService.eliminarTodos();

        verify(promocionesRepository, times(1)).deleteAll();
    }
}
