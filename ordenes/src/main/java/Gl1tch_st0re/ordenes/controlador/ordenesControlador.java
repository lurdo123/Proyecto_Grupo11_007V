package Gl1tch_st0re.ordenes.controlador;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.servicio.ordenesServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Creación y seguimiento de compras con validación de disponibilidad en catálogo")
public class ordenesControlador {

    @Autowired
    private ordenesServicio ordenesServicio;

    @Operation(summary = "Listar órdenes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de órdenes"),
            @ApiResponse(responseCode = "204", description = "Sin órdenes registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<ordenesModelo>> listar() {
        List<ordenesModelo> lista = ordenesServicio.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener orden por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesServicio.findById(id));
    }

    @Operation(summary = "Listar órdenes por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Órdenes del usuario"),
            @ApiResponse(responseCode = "404", description = "Usuario sin órdenes"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(ordenesServicio.findByUsuario(usuario));
    }

    @Operation(summary = "Listar órdenes por estado", description = "El estado se normaliza a mayúsculas (PENDIENTE, ENVIADA, ENTREGADA, CANCELADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Órdenes con el estado indicado"),
            @ApiResponse(responseCode = "404", description = "Sin órdenes con ese estado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenesServicio.findByEstado(estado));
    }

    @Operation(summary = "Crear orden", description = "Crea una orden validando disponibilidad y stock en catálogo. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, producto no disponible o stock insuficiente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ordenesRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        ordenesModelo creada = ordenesServicio.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Orden creada correctamente",
                "id", creada.getId(),
                "usuario", creada.getUsuario(),
                "producto", creada.getProducto(),
                "cantidad", creada.getCantidad(),
                "estado", creada.getEstado(),
                "fechaCreacion", creada.getFechaCreacion().toString()));
    }

    @Operation(summary = "Actualizar orden")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
            @Valid @RequestBody ordenesRequestDTO dto) {
        ordenesModelo actualizada = ordenesServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Orden con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "usuario", actualizada.getUsuario(),
                "producto", actualizada.getProducto(),
                "cantidad", actualizada.getCantidad(),
                "estado", actualizada.getEstado(),
                "fechaCreacion", actualizada.getFechaCreacion().toString()));
    }

    @Operation(summary = "Eliminar orden por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = ordenesServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todas las órdenes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las órdenes eliminadas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = ordenesServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
