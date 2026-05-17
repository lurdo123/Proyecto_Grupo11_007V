package Gl1tch_st0re.clientes.controller;

import Gl1tch_st0re.clientes.dto.request.perfilClienteRequestDTO;
import Gl1tch_st0re.clientes.model.perfilClienteModel;
import Gl1tch_st0re.clientes.service.perfilClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes/perfiles")
public class perfilClienteController {

    @Autowired
    private perfilClienteService perfilClienteService;

    @GetMapping
    public ResponseEntity<List<perfilClienteModel>> listar() {
        List<perfilClienteModel> lista = perfilClienteService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(perfilClienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody perfilClienteRequestDTO dto) {
        perfilClienteModel creado = perfilClienteService.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Perfil de cliente creado correctamente",
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
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody perfilClienteRequestDTO dto) {
        perfilClienteModel actualizado = perfilClienteService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Perfil con id " + id + " actualizado correctamente",
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
        return ResponseEntity.ok(Map.of("mensaje", perfilClienteService.eliminar(id)));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", perfilClienteService.eliminarTodos()));
    }
}