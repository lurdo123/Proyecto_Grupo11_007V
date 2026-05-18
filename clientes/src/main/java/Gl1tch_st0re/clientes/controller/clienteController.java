package Gl1tch_st0re.clientes.controller;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.service.clienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class clienteController {

    @Autowired
    private clienteService clienteService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminar(id)));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", clienteService.eliminarTodos()));
    }
}