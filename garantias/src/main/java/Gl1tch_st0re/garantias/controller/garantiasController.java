package Gl1tch_st0re.garantias.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.service.garantiasService;
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
@RequestMapping("/api/garantias")
@Tag(name = "Garantías", description = "Registro y administración de garantías de productos con validación en catálogo")
public class garantiasController {

    @Autowired
    private garantiasService garantiasService;

    @Operation(summary = "Listar garantías")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de garantías"),
            @ApiResponse(responseCode = "204", description = "Sin garantías registradas"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Garantía encontrada"),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(garantiasService.obtenerPorId(id));
    }

    @Operation(summary = "Crear garantía", description = "Crea una garantía verificando que el producto exista en el catálogo. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Garantía creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en catálogo"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "200", description = "Garantía actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody garantiasRequestDTO dto) {
        return ResponseEntity.ok(garantiasService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar garantía por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Garantía eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Garantía no encontrada"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
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
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        garantiasService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}
