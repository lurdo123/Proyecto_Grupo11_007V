package Gl1tch_st0re.pagos.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.pagos.dto.request.pagoRequestDTO;
import Gl1tch_st0re.pagos.model.pagoModel;
import Gl1tch_st0re.pagos.service.pagoService;
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
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Procesamiento y registro de transacciones con validación de órdenes")
public class pagoController {

    @Autowired
    private pagoService pagoService;

    @Operation(summary = "Listar pagos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos"),
            @ApiResponse(responseCode = "204", description = "Sin pagos registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<pagoModel>> listar() {
        List<pagoModel> lista = pagoService.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener pago por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear pago", description = "Registra un pago verificando que la orden exista. El id de transacción externa debe ser único. Requiere header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o transacción duplicada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada en el servicio de órdenes"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody pagoRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        pagoModel creado = pagoService.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Pago creado correctamente",
                "id", creado.getId(),
                "ordenId", creado.getOrdenId(),
                "idTransaccionExterna", creado.getIdTransaccionExterna(),
                "metodoPago", creado.getMetodoPago(),
                "montoPagado", creado.getMontoPagado(),
                "estadoPago", creado.getEstadoPago()));
    }

    @Operation(summary = "Actualizar pago")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o transacción duplicada en otro pago"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody pagoRequestDTO dto) {
        pagoModel actualizado = pagoService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Pago con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "ordenId", actualizado.getOrdenId(),
                "idTransaccionExterna", actualizado.getIdTransaccionExterna(),
                "metodoPago", actualizado.getMetodoPago(),
                "montoPagado", actualizado.getMontoPagado(),
                "estadoPago", actualizado.getEstadoPago()));
    }

    @Operation(summary = "Eliminar pago por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", pagoService.eliminar(id)));
    }

    @Operation(summary = "Eliminar todos los pagos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los pagos eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", pagoService.eliminarTodos()));
    }
}
