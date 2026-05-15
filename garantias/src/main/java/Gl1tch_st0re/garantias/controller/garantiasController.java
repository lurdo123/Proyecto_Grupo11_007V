package Gl1tch_st0re.garantias.controller;

import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.service.garantiasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garantias")
public class garantiasController {

    @Autowired
    private garantiasService garantiasService;

    @GetMapping
    public ResponseEntity<List<garantiasModel>> listar() {
        List<garantiasModel> garantias = garantiasService.findAll();
        if (garantias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(garantias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(garantiasService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody garantiasRequestDTO dto) {
        return ResponseEntity.status(201).body(garantiasService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody garantiasRequestDTO dto) {
        return ResponseEntity.ok(garantiasService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        garantiasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        garantiasService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}