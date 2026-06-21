package Gl1tch_st0re.clientes.controller;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.service.clienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de perfiles de clientes y niveles de fidelidad")
public class clienteController {

    @Autowired
    private clienteService clienteService;

    @Operation(summary = "Listar clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Clientes encontrados correctamente\",\"total\":1,\"clientes\":[{\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}]}"))),
            @ApiResponse(responseCode = "204", description = "Sin clientes registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Clientes encontrados correctamente\",\"total\":1,\"clientes\":[{\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}]}"))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Cliente creado correctamente\",\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario_id ya tiene perfil",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Clientes encontrados correctamente\",\"total\":1,\"clientes\":[{\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario_id duplicado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":400,\"error\":\"Bad Request\",\"mensaje\":\"El campo requerido no puede estar vacío\",\"ruta\":\"/api/recurso\"}"))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
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
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Clientes encontrados correctamente\",\"total\":1,\"clientes\":[{\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}]}"))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":404,\"error\":\"Not Found\",\"mensaje\":\"Recurso con id 99 no encontrado\",\"ruta\":\"/api/recurso/99\"}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminar(id)));
    }

    @Operation(summary = "Eliminar todos los clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todos los clientes eliminados",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Clientes encontrados correctamente\",\"total\":1,\"clientes\":[{\"id\":1,\"usuarioId\":1,\"nombre\":\"Juan\",\"apellido\":\"Pérez\",\"telefono\":\"+56912345678\",\"nivelFidelidad\":\"Bronce\",\"totalCompradoHistorico\":0.0}]}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"fecha\":\"2026-06-20T22:00:00\",\"status\":401,\"error\":\"Unauthorized\",\"mensaje\":\"Token JWT inválido o no proporcionado\",\"ruta\":\"/api/recurso\"}")))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminarTodos()));
    }
}
