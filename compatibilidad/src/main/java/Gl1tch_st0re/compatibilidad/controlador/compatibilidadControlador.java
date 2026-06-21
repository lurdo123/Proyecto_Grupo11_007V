package Gl1tch_st0re.compatibilidad.controlador;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.servicio.compatibilidadServicio;
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
@RequestMapping("/api/compatibilidades")
@Tag(name = "Compatibilidad", description = "Registro y verificación de compatibilidad entre componentes de hardware")
public class compatibilidadControlador {

    @Autowired
    private compatibilidadServicio compatibilidadServicio;

    @Operation(summary = "Listar compatibilidades")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de compatibilidades",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
            @ApiResponse(responseCode = "204", description = "Sin registros"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<compatibilidadModelo>> listar() {
        List<compatibilidadModelo> lista = compatibilidadServicio.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener compatibilidad por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
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
        return ResponseEntity.ok(compatibilidadServicio.findById(id));
    }

    @Operation(summary = "Crear compatibilidad", description = "Registra una nueva pareja componenteBase/componenteCompatible. La pareja debe ser única")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compatibilidad creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Compatibilidad creada correctamente\",\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja duplicada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody compatibilidadRequestDTO dto) {
        compatibilidadModelo creado = compatibilidadServicio.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Compatibilidad creada correctamente",
                "id", creado.getId(),
                "componenteBase", creado.getComponenteBase(),
                "componenteCompatible", creado.getComponenteCompatible(),
                "tipo", creado.getTipo()
        ));
    }

    @Operation(summary = "Actualizar compatibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compatibilidad actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja duplicada en otro registro",
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
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody compatibilidadRequestDTO dto) {
        compatibilidadModelo actualizado = compatibilidadServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Compatibilidad con id " + id + " actualizada correctamente",
                "id", actualizado.getId(),
                "componenteBase", actualizado.getComponenteBase(),
                "componenteCompatible", actualizado.getComponenteCompatible(),
                "tipo", actualizado.getTipo()
        ));
    }

    @Operation(summary = "Eliminar compatibilidad por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
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
        String mensaje = compatibilidadServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todos los registros de compatibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los registros eliminados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = compatibilidadServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Verificar compatibilidad", description = "Consulta si dos componentes son compatibles entre sí")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación (esCompatible: true/false)",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\":1,\"componenteBase\":\"RTX 4080\",\"componenteCompatible\":\"PCIe 4.0\",\"tipo\":\"SLOT\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/verificar")
    public ResponseEntity<?> verificar(
            @RequestParam String componenteBase,
            @RequestParam String componenteCompatible) {
        boolean compatible = compatibilidadServicio.verificarCompatibilidad(componenteBase, componenteCompatible);
        return ResponseEntity.ok(Map.of(
                "componenteBase", componenteBase,
                "componenteCompatible", componenteCompatible,
                "esCompatible", compatible
        ));
    }
}
