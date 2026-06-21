package Gl1tch_st0re.inventario.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.inventario.dto.request.inventarioRequestDTO;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.service.inventarioService;
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
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Control de stock físico, estado y ubicación en bodega con validación en catálogo")
public class inventarioController {

    @Autowired
    private inventarioService inventarioService;

    @Operation(summary = "Listar registros de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de registros de inventario",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin registros"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<inventarioModel>> listar() {
        List<inventarioModel> lista = inventarioService.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener registro de inventario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}]"))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @Operation(summary = "Crear registro de inventario", description = "Crea un registro verificando que el producto exista en catálogo y no tenga inventario previo. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Inventario creado correctamente\",\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o producto ya tiene inventario",
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
    public ResponseEntity<?> crear(@Valid @RequestBody inventarioRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        inventarioModel creado = inventarioService.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Inventario creado correctamente",
                "id", creado.getId(),
                "productoId", creado.getProductoId(),
                "estadoFisico", creado.getEstadoFisico(),
                "cantidadDisponible", creado.getCantidadDisponible(),
                "ubicacionBodega", creado.getUbicacionBodega()));
    }

    @Operation(summary = "Actualizar registro de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o productoId duplicado en otro registro",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody inventarioRequestDTO dto) {
        inventarioModel actualizado = inventarioService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Inventario con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "productoId", actualizado.getProductoId(),
                "estadoFisico", actualizado.getEstadoFisico(),
                "cantidadDisponible", actualizado.getCantidadDisponible(),
                "ubicacionBodega", actualizado.getUbicacionBodega()));
    }

    @Operation(summary = "Eliminar registro de inventario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}]"))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = inventarioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todos los registros de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los registros eliminados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"estadoFisico\":\"NUEVO\",\"cantidadDisponible\":10,\"ubicacionBodega\":\"A-01\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = inventarioService.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
