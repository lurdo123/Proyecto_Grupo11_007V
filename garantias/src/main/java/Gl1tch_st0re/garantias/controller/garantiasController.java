package Gl1tch_st0re.garantias.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.service.garantiasService;
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

@RestController
@RequestMapping("/api/garantias")
@Tag(name = "Garantías", description = "Registro y administración de garantías de productos con validación en catálogo")
public class garantiasController {

    @Autowired
    private garantiasService garantiasService;

    @Operation(summary = "Listar garantías")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de garantías",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"ordenId\":1,\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2027-01-01\",\"descripcion\":\"Garantía de fábrica 12 meses\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin garantías registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<garantiasModel>> listar() {
        List<garantiasModel> garantias = garantiasService.findAll();
        if (garantias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(garantias);
    }

    @Operation(summary = "Obtener garantía por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Garantía encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"ordenId\":1,\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2027-01-01\",\"descripcion\":\"Garantía de fábrica 12 meses\"}]"))),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(garantiasService.obtenerPorId(id));
    }

    @Operation(summary = "Crear garantía", description = "Crea una garantía verificando que el producto exista en el catálogo. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Garantía creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\":1,\"productoId\":1,\"ordenId\":1,\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2027-01-01\",\"descripcion\":\"Garantía de fábrica 12 meses\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
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
    public ResponseEntity<?> crear(@Valid @RequestBody garantiasRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.status(201).body(garantiasService.crear(dto, token));
    }

    @Operation(summary = "Actualizar garantía")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Garantía actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"productoId\":1,\"ordenId\":1,\"fechaInicio\":\"2026-01-01\",\"fechaFin\":\"2027-01-01\",\"descripcion\":\"Garantía de fábrica 12 meses\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody garantiasRequestDTO dto) {
        return ResponseEntity.ok(garantiasService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar garantía por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Garantía eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        garantiasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todas las garantías")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todas las garantías eliminadas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        garantiasService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}
