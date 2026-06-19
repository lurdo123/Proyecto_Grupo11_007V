package Gl1tch_st0re.envios.controlador;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.envios.dto.request.enviosRequestDTO;
import Gl1tch_st0re.envios.modelo.enviosModelo;
import Gl1tch_st0re.envios.servicio.enviosServicio;
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
@RequestMapping("/api/envios")
@Tag(name = "Envíos", description = "Gestión de despachos y seguimiento por orden, usuario y estado")
public class enviosControlador {

    @Autowired
    private enviosServicio enviosServicio;

    @Operation(summary = "Listar envíos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de envíos"),
            @ApiResponse(responseCode = "204", description = "Sin envíos registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<enviosModelo>> listar() {
        List<enviosModelo> lista = enviosServicio.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener envío por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(enviosServicio.findById(id));
    }

    @Operation(summary = "Listar envíos por usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envíos del usuario"),
            @ApiResponse(responseCode = "404", description = "Usuario sin envíos"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<enviosModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(enviosServicio.findByUsuario(usuario));
    }

    @Operation(summary = "Listar envíos por estado", description = "El estado se normaliza a mayúsculas (PENDIENTE, EN_TRANSITO, ENTREGADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envíos con el estado indicado"),
            @ApiResponse(responseCode = "404", description = "Sin envíos con ese estado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<enviosModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(enviosServicio.findByEstado(estado));
    }

    @Operation(summary = "Listar envíos por orden")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envíos asociados a la orden"),
            @ApiResponse(responseCode = "404", description = "Orden sin envíos asociados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<enviosModelo>> obtenerPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(enviosServicio.findByOrdenId(ordenId));
    }

    @Operation(summary = "Crear envío", description = "Crea un envío validando que la orden exista en el microservicio de órdenes. Requiere header Authorization con token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envío creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada en el servicio de órdenes"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody enviosRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        enviosModelo creado = enviosServicio.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Envío creado correctamente",
                "id", creado.getId(),
                "ordenId", creado.getOrdenId(),
                "usuario", creado.getUsuario(),
                "direccion", creado.getDireccion(),
                "estado", creado.getEstado(),
                "transportista", creado.getTransportista(),
                "fechaEnvio", creado.getFechaEnvio().toString(),
                "fechaEntregaEstimada", creado.getFechaEntregaEstimada().toString()));
    }

    @Operation(summary = "Actualizar envío")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
            @Valid @RequestBody enviosRequestDTO dto) {
        enviosModelo actualizado = enviosServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Envío con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "ordenId", actualizado.getOrdenId(),
                "usuario", actualizado.getUsuario(),
                "direccion", actualizado.getDireccion(),
                "estado", actualizado.getEstado(),
                "transportista", actualizado.getTransportista(),
                "fechaEnvio", actualizado.getFechaEnvio().toString(),
                "fechaEntregaEstimada", actualizado.getFechaEntregaEstimada().toString()));
    }

    @Operation(summary = "Eliminar envío por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = enviosServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @Operation(summary = "Eliminar todos los envíos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los envíos eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = enviosServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}
