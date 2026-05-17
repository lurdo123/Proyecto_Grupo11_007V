package Gl1tch_st0re.promociones.controller;

import Gl1tch_st0re.promociones.dto.request.promocionesRequestDTO;
import Gl1tch_st0re.promociones.model.promocionesModel;
import Gl1tch_st0re.promociones.service.promocionesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
public class promocionesController {

    @Autowired
    private promocionesService promocionesService;

    @GetMapping
    public ResponseEntity<List<promocionesModel>> listar() {
        List<promocionesModel> promociones = promocionesService.findAll();
        if (promociones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(promociones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(promocionesService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody promocionesRequestDTO dto) {
        return ResponseEntity.status(201).body(promocionesService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody promocionesRequestDTO dto) {
        return ResponseEntity.ok(promocionesService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        promocionesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        promocionesService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}