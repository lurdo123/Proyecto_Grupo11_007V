package Gl1tch_st0re.preventas.controlador;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.servicio.preventasServicio;
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
@RequestMapping("/api/preventas")
@Tag(name = "Preventas", description = "Gestión de reservas anticipadas de productos antes de su lanzamiento oficial")
public class preventasControlador {

    @Autowired
    private preventasServicio preventasServicio;

    @Operation(summary = "Listar preventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de preventas"),
            @ApiResponse(responseCode = "204", description = "Sin preventas registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<preventasModelo>> listar() {
        List<preventasModelo> lista = preventasServicio.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener preventa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventa encontrada"),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(preventasServicio.findById(id));
    }

    @Operation(summary = "Listar preventas por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventas del usuario"),
            @ApiResponse(responseCode = "404", description = "Usuario sin preventas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<preventasModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(preventasServicio.findByUsuario(usuario));
    }

    @Operation(summary = "Listar preventas por estado", description = "El estado se normaliza a mayúsculas (RESERVADO, CONFIRMADO, CANCELADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventas con el estado indicado"),
            @ApiResponse(responseCode = "404", description = "Sin preventas con ese estado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<preventasModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(preventasServicio.findByEstado(estado));
    }

    @Operation(summary = "Crear preventa", description = "Registra una reserva anticipada. El estado se normaliza a mayúsculas automáticamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Preventa creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody preventasRequestDTO dto) {
        preventasModelo creada = preventasServicio.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Preventa creada correctamente",
                "id", creada.getId(),
                "usuario", creada.getUsuario(),
                "producto", creada.getProducto(),
                "cantidad", creada.getCantidad(),
                "estado", creada.getEstado(),
                "fechaReserva", creada.getFechaReserva().toString(),
                "fechaLanzamiento", creada.getFechaLanzamiento().toString()
        ));
    }

    @Operation(summary = "Actualizar preventa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventa actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody preventasRequestDTO dto) {
        preventasModelo actualizada = preventasServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Preventa con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "usuario", actualizada.getUsuario(),
                "producto", actualizada.getProducto(),
                "cantidad", actualizada.getCantidad(),
                "estado", actualizada.getEstado(),
                "fechaReserva", actualizada.getFechaReserva().toString(),
                "fechaLanzamiento", actualizada.getFechaLanzamiento().toString()
        ));
    }

    @Operation(summary = "Eliminar preventa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventa eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = preventasServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todas las preventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las preventas eliminadas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = preventasServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
