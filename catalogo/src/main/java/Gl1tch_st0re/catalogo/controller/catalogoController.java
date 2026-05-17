package Gl1tch_st0re.catalogo.controller;

import Gl1tch_st0re.catalogo.dto.request.catalogoRequestDTO;
import Gl1tch_st0re.catalogo.model.catalogoModel;
import Gl1tch_st0re.catalogo.service.catalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class catalogoController {

    @Autowired
    private catalogoService catalogoService;

    @GetMapping
    public ResponseEntity<List<catalogoModel>> listar() {
        List<catalogoModel> productos = catalogoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody catalogoRequestDTO dto) {
        return ResponseEntity.status(201).body(catalogoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody catalogoRequestDTO dto) {
        return ResponseEntity.ok(catalogoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        catalogoService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}