package Gl1tch_st0re.resenas.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.service.resenaService;
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
@RequestMapping("/api/resenas")
@Tag(name = "Reseñas", description = "Publicación y gestión de opiniones de productos con validación en catálogo")
public class resenaController {

    @Autowired
    private resenaService resenaService;

    @Operation(summary = "Listar reseñas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reseñas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin reseñas registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<resenaModel>> listar() {
        List<resenaModel> lista = resenaService.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener reseña por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}]"))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(summary = "Crear reseña", description = "Publica una reseña verificando que el producto exista en catálogo. Un usuario solo puede tener una reseña por producto. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Resena creada correctamente\",\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reseña duplicada para usuario/producto",
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
    public ResponseEntity<?> crear(@Valid @RequestBody resenaRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        resenaModel creada = resenaService.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Resena creada correctamente",
                "id", creada.getId(),
                "productoId", creada.getProductoId(),
                "usuarioId", creada.getUsuarioId(),
                "calificacion", creada.getCalificacion(),
                "comentario", creada.getComentario(),
                "esCompraVerificada", creada.getEsCompraVerificada(),
                "fechaPublicacion", creada.getFechaPublicacion().toString()));
    }

    @Operation(summary = "Actualizar reseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja usuario/producto duplicada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody resenaRequestDTO dto) {
        resenaModel actualizada = resenaService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Resena con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "productoId", actualizada.getProductoId(),
                "usuarioId", actualizada.getUsuarioId(),
                "calificacion", actualizada.getCalificacion(),
                "comentario", actualizada.getComentario(),
                "esCompraVerificada", actualizada.getEsCompraVerificada(),
                "fechaPublicacion", actualizada.getFechaPublicacion().toString()));
    }

    @Operation(summary = "Eliminar reseña por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}]"))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminar(id)));
    }

    @Operation(summary = "Eliminar todas las reseñas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las reseñas eliminadas",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"usuarioId\":1,\"calificacion\":5,\"comentario\":\"Excelente producto\",\"esCompraVerificada\":false,\"fechaPublicacion\":\"2026-06-20\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminarTodos()));
    }
}
