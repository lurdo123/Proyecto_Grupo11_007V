package Gl1tch_st0re.preventas.controlador;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.servicio.preventasServicio;
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
@RequestMapping("/api/preventas")
@Tag(name = "Preventas", description = "Gestión de reservas anticipadas de productos antes de su lanzamiento oficial")
public class preventasControlador {

    @Autowired
    private preventasServicio preventasServicio;

    @Operation(summary = "Listar preventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de preventas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin preventas registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Preventa encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(preventasServicio.findById(id));
    }

    @Operation(summary = "Listar preventas por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventas del usuario",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Usuario sin preventas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<preventasModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(preventasServicio.findByUsuario(usuario));
    }

    @Operation(summary = "Listar preventas por estado", description = "El estado se normaliza a mayúsculas (RESERVADO, CONFIRMADO, CANCELADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preventas con el estado indicado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Sin preventas con ese estado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<preventasModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(preventasServicio.findByEstado(estado));
    }

    @Operation(summary = "Crear preventa", description = "Registra una reserva anticipada. El estado se normaliza a mayúsculas automáticamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Preventa creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Preventa creada correctamente\",\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Preventa actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Preventa eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Preventa no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = preventasServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todas las preventas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las preventas eliminadas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"usuario\":\"jonatan\",\"producto\":\"RTX 5090 Ti\",\"cantidad\":1,\"estado\":\"RESERVADO\",\"fechaReserva\":\"2026-06-20T22:00:00\",\"fechaLanzamiento\":\"2026-09-01T00:00:00\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = preventasServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
