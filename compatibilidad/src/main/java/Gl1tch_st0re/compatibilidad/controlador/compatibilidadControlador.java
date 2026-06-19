package Gl1tch_st0re.compatibilidad.controlador;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.servicio.compatibilidadServicio;
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
@RequestMapping("/api/compatibilidades")
@Tag(name = "Compatibilidad", description = "Registro y verificación de compatibilidad entre componentes de hardware")
public class compatibilidadControlador {

    @Autowired
    private compatibilidadServicio compatibilidadServicio;

    @Operation(summary = "Listar compatibilidades")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de compatibilidades"),
            @ApiResponse(responseCode = "204", description = "Sin registros"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compatibilidadServicio.findById(id));
    }

    @Operation(summary = "Crear compatibilidad", description = "Registra una nueva pareja componenteBase/componenteCompatible. La pareja debe ser única")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compatibilidad creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja duplicada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Compatibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pareja duplicada en otro registro"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Registro eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = compatibilidadServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todos los registros de compatibilidad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los registros eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = compatibilidadServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Verificar compatibilidad", description = "Consulta si dos componentes son compatibles entre sí")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación (esCompatible: true/false)"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
