package Gl1tch_st0re.catalogo.controller;

import Gl1tch_st0re.catalogo.dto.request.catalogoRequestDTO;
import Gl1tch_st0re.catalogo.model.catalogoModel;
import Gl1tch_st0re.catalogo.service.catalogoService;
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
@RequestMapping("/api/catalogo")
@Tag(name = "Catálogo", description = "Gestión de productos: nombre, precio, stock, categoría y marca")
public class catalogoController {

    @Autowired
    private catalogoService catalogoService;

    @Operation(summary = "Listar productos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"RTX 4080\",\"precio\":899990.0,\"stock\":5,\"categoria\":\"GPU\",\"marca\":\"NVIDIA\",\"disponible\":true}]"))),
            @ApiResponse(responseCode = "204", description = "Sin productos registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<catalogoModel>> listar() {
        List<catalogoModel> productos = catalogoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"RTX 4080\",\"precio\":899990.0,\"stock\":5,\"categoria\":\"GPU\",\"marca\":\"NVIDIA\",\"disponible\":true}]"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"RTX 4080\",\"precio\":899990.0,\"stock\":5,\"categoria\":\"GPU\",\"marca\":\"NVIDIA\",\"disponible\":true}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody catalogoRequestDTO dto) {
        return ResponseEntity.status(201).body(catalogoService.crear(dto));
    }

    @Operation(summary = "Actualizar producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"RTX 4080\",\"precio\":899990.0,\"stock\":5,\"categoria\":\"GPU\",\"marca\":\"NVIDIA\",\"disponible\":true}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody catalogoRequestDTO dto) {
        return ResponseEntity.ok(catalogoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todos los productos")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todos los productos eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        catalogoService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}
