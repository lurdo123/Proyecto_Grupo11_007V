package Gl1tch_st0re.clientes.controller;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.service.clienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de perfiles de clientes y niveles de fidelidad")
public class clienteController {

    @Autowired
    private clienteService clienteService;

    @Operation(summary = "Listar clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes"),
            @ApiResponse(responseCode = "204", description = "Sin clientes registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<?> listar() {
        List<clienteModel> lista = clienteService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Clientes encontrados correctamente",
                "total", lista.size(),
                "clientes", lista
        ));
    }

    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        clienteModel cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Cliente encontrado correctamente",
                "id", cliente.getId(),
                "usuarioId", cliente.getUsuarioId(),
                "nombre", cliente.getNombre(),
                "apellido", cliente.getApellido(),
                "telefono", cliente.getTelefono(),
                "nivelFidelidad", cliente.getNivelFidelidad(),
                "totalCompradoHistorico", cliente.getTotalCompradoHistorico()
        ));
    }

    @Operation(summary = "Crear cliente", description = "Registra un nuevo perfil de cliente. Si no se especifica nivel de fidelidad, se asigna 'Bronce'")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario_id ya tiene perfil"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody clienteRequestDTO dto) {
        clienteModel creado = clienteService.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Cliente creado correctamente",
                "id", creado.getId(),
                "usuarioId", creado.getUsuarioId(),
                "nombre", creado.getNombre(),
                "apellido", creado.getApellido(),
                "telefono", creado.getTelefono(),
                "nivelFidelidad", creado.getNivelFidelidad(),
                "totalCompradoHistorico", creado.getTotalCompradoHistorico()
        ));
    }

    @Operation(summary = "Actualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario_id duplicado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody clienteRequestDTO dto) {
        clienteModel actualizado = clienteService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Cliente con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "usuarioId", actualizado.getUsuarioId(),
                "nombre", actualizado.getNombre(),
                "apellido", actualizado.getApellido(),
                "telefono", actualizado.getTelefono(),
                "nivelFidelidad", actualizado.getNivelFidelidad(),
                "totalCompradoHistorico", actualizado.getTotalCompradoHistorico()
        ));
    }

    @Operation(summary = "Eliminar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminar(id)));
    }

    @Operation(summary = "Eliminar todos los clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los clientes eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminarTodos()));
    }
}
