package Gl1tch_st0re.resenas.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.service.resenaService;
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
@RequestMapping("/api/resenas")
@Tag(name = "Reseñas", description = "Publicación y gestión de opiniones de productos con validación en catálogo")
public class resenaController {

    @Autowired
    private resenaService resenaService;

    @Operation(summary = "Listar reseñas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reseñas"),
            @ApiResponse(responseCode = "204", description = "Sin reseñas registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(summary = "Crear reseña", description = "Publica una reseña verificando que el producto exista en catálogo. Un usuario solo puede tener una reseña por producto. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o reseña duplicada para usuario/producto"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja usuario/producto duplicada"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Reseña eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminar(id)));
    }

    @Operation(summary = "Eliminar todas las reseñas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las reseñas eliminadas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminarTodos()));
    }
}
