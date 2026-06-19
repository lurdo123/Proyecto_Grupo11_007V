package Gl1tch_st0re.catalogo.service;

import Gl1tch_st0re.catalogo.dto.request.catalogoRequestDTO;
import Gl1tch_st0re.catalogo.exceptions.catalogoNotFoundException;
import Gl1tch_st0re.catalogo.model.catalogoModel;
import Gl1tch_st0re.catalogo.repository.catalogoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de catálogo.
 *
 * Se mockea el repositorio JPA para aislar y validar exclusivamente
 * la lógica de negocio contenida en catalogoService (sin levantar
 * contexto de Spring ni base de datos).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("catalogoService - pruebas unitarias")
class catalogoServiceTest {

    @Mock
    private catalogoRepository catalogoRepository;

    @InjectMocks
    private catalogoService catalogoService;

    private catalogoModel productoExistente;
    private catalogoRequestDTO dtoValido;

    @BeforeEach
    void setUp() {
        productoExistente = catalogoModel.builder()
                .id(1L)
                .nombre("Teclado Mecánico RGB")
                .descripcion("Switches azules")
                .precio(new BigDecimal("45000"))
                .stock(20)
                .categoria("Periféricos")
                .marca("Logitech")
                .disponible(true)
                .build();

        dtoValido = new catalogoRequestDTO();
        dtoValido.setNombre("Mouse Gamer");
        dtoValido.setDescripcion("Sensor óptico 16000 DPI");
        dtoValido.setPrecio(new BigDecimal("25000"));
        dtoValido.setStock(15);
        dtoValido.setCategoria("Periféricos");
        dtoValido.setMarca("Razer");
        dtoValido.setDisponible(true);
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll retorna todos los productos registrados")
    void findAll_retornaListaCompleta() {
        catalogoModel otro = catalogoModel.builder().id(2L).nombre("Monitor 24''").build();
        when(catalogoRepository.findAll()).thenReturn(List.of(productoExistente, otro));

        List<catalogoModel> resultado = catalogoService.findAll();

        assertEquals(2, resultado.size());
        verify(catalogoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll retorna lista vacía cuando no hay productos")
    void findAll_sinProductos_retornaListaVacia() {
        when(catalogoRepository.findAll()).thenReturn(List.of());

        List<catalogoModel> resultado = catalogoService.findAll();

        assertTrue(resultado.isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    @DisplayName("obtenerPorId retorna el producto cuando existe")
    void obtenerPorId_existente_retornaProducto() {
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));

        catalogoModel resultado = catalogoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Teclado Mecánico RGB", resultado.getNombre());
    }

    @Test
    @DisplayName("obtenerPorId lanza catalogoNotFoundException cuando no existe")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(catalogoRepository.findById(99L)).thenReturn(Optional.empty());

        catalogoNotFoundException ex = assertThrows(catalogoNotFoundException.class,
                () -> catalogoService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear guarda el producto con todos los campos del DTO")
    void crear_casoExitoso() {
        when(catalogoRepository.save(any(catalogoModel.class))).thenAnswer(invocacion -> {
            catalogoModel modelo = invocacion.getArgument(0);
            modelo.setId(10L);
            return modelo;
        });

        catalogoModel resultado = catalogoService.crear(dtoValido);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(new BigDecimal("25000"), resultado.getPrecio());
        assertEquals(15, resultado.getStock());
        assertEquals("Razer", resultado.getMarca());
        assertTrue(resultado.getDisponible());
        verify(catalogoRepository, times(1)).save(any(catalogoModel.class));
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar modifica todos los campos del producto existente")
    void actualizar_casoExitoso() {
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));
        when(catalogoRepository.save(any(catalogoModel.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        catalogoRequestDTO dtoActualizacion = new catalogoRequestDTO();
        dtoActualizacion.setNombre("Teclado Mecánico RGB v2");
        dtoActualizacion.setDescripcion("Switches rojos");
        dtoActualizacion.setPrecio(new BigDecimal("52000"));
        dtoActualizacion.setStock(8);
        dtoActualizacion.setCategoria("Periféricos");
        dtoActualizacion.setMarca("Logitech");
        dtoActualizacion.setDisponible(false);

        catalogoModel resultado = catalogoService.actualizar(1L, dtoActualizacion);

        assertEquals("Teclado Mecánico RGB v2", resultado.getNombre());
        assertEquals("Switches rojos", resultado.getDescripcion());
        assertEquals(new BigDecimal("52000"), resultado.getPrecio());
        assertEquals(8, resultado.getStock());
        assertFalse(resultado.getDisponible());
        verify(catalogoRepository, times(1)).save(productoExistente);
    }

    @Test
    @DisplayName("actualizar lanza excepción si el producto no existe")
    void actualizar_inexistente_lanzaExcepcion() {
        when(catalogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(catalogoNotFoundException.class,
                () -> catalogoService.actualizar(99L, dtoValido));
        verify(catalogoRepository, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar borra el producto existente")
    void eliminar_casoExitoso() {
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));

        catalogoService.eliminar(1L);

        verify(catalogoRepository, times(1)).delete(productoExistente);
    }

    @Test
    @DisplayName("eliminar lanza excepción si el producto no existe")
    void eliminar_inexistente_lanzaExcepcion() {
        when(catalogoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(catalogoNotFoundException.class,
                () -> catalogoService.eliminar(99L));
        verify(catalogoRepository, never()).delete(any());
    }

    // ---------- eliminarTodos ----------

    @Test
    @DisplayName("eliminarTodos borra todos los productos registrados")
    void eliminarTodos_casoExitoso() {
        when(catalogoRepository.count()).thenReturn(12L);

        catalogoService.eliminarTodos();

        verify(catalogoRepository, times(1)).deleteAll();
    }
}