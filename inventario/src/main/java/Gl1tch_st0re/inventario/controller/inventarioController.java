package Gl1tch_st0re.inventario.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.inventario.dto.request.inventarioRequestDTO;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.service.inventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class inventarioController {

    @Autowired
    private inventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<inventarioModel>> listar() {
        List<inventarioModel> lista = inventarioService.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody inventarioRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        inventarioModel creado = inventarioService.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Inventario creado correctamente",
                "id", creado.getId(),
                "productoId", creado.getProductoId(),
                "estadoFisico", creado.getEstadoFisico(),
                "cantidadDisponible", creado.getCantidadDisponible(),
                "ubicacionBodega", creado.getUbicacionBodega()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody inventarioRequestDTO dto) {
        inventarioModel actualizado = inventarioService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Inventario con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "productoId", actualizado.getProductoId(),
                "estadoFisico", actualizado.getEstadoFisico(),
                "cantidadDisponible", actualizado.getCantidadDisponible(),
                "ubicacionBodega", actualizado.getUbicacionBodega()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = inventarioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = inventarioService.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}