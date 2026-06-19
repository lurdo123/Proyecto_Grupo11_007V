package Gl1tch_st0re.promociones.controller;

import Gl1tch_st0re.promociones.dto.request.promocionesRequestDTO;
import Gl1tch_st0re.promociones.model.promocionesModel;
import Gl1tch_st0re.promociones.service.promocionesService;
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

@RestController
@RequestMapping("/api/promociones")
@Tag(name = "Promociones", description = "Administración de códigos de descuento y vigencia de ofertas")
public class promocionesController {

    @Autowired
    private promocionesService promocionesService;

    @Operation(summary = "Listar promociones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de promociones"),
            @ApiResponse(responseCode = "204", description = "Sin promociones registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<promocionesModel>> listar() {
        List<promocionesModel> promociones = promocionesService.findAll();
        if (promociones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(promociones);
    }

    @Operation(summary = "Obtener promoción por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(promocionesService.obtenerPorId(id));
    }

    @Operation(summary = "Crear promoción")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody promocionesRequestDTO dto) {
        return ResponseEntity.status(201).body(promocionesService.crear(dto));
    }

    @Operation(summary = "Actualizar promoción")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody promocionesRequestDTO dto) {
        return ResponseEntity.ok(promocionesService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar promoción por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Promoción no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        promocionesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todas las promociones")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todas las promociones eliminadas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        promocionesService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}
