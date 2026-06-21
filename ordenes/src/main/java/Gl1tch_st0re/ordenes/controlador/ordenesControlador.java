package Gl1tch_st0re.ordenes.controlador;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.servicio.ordenesServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            @ApiResponse(responseCode = "200", description = "Lista de órdenes",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin órdenes registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Orden encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesServicio.findById(id));
    }

    @Operation(summary = "Listar órdenes por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Órdenes del usuario",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Usuario sin órdenes",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(ordenesServicio.findByUsuario(usuario));
    }

    @Operation(summary = "Listar órdenes por estado", description = "El estado se normaliza a mayúsculas (PENDIENTE, ENVIADA, ENTREGADA, CANCELADA)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Órdenes con el estado indicado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Sin órdenes con ese estado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenesServicio.findByEstado(estado));
    }

    @Operation(summary = "Crear orden", description = "Crea una orden validando disponibilidad y stock en catálogo. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Orden creada correctamente\",\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, producto no disponible o stock insuficiente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Orden actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Orden eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = ordenesServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todas las órdenes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las órdenes eliminadas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 4080\",\"cantidad\":1,\"estado\":\"PENDIENTE\",\"fechaCreacion\":\"2026-06-20T22:00:00\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = ordenesServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
