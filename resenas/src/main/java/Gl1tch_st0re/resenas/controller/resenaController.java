package Gl1tch_st0re.resenas.controller;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.service.resenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class resenaController {

    @Autowired
    private resenaService resenaService;

    @GetMapping
    public ResponseEntity<List<resenaModel>> listar() {
        List<resenaModel> lista = resenaService.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody resenaRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        resenaModel creada = resenaService.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Resena creada correctamente",
                "id", creada.getId(),
                "productoId", creada.getProductoId(),
                "usuarioId", creada.getUsuarioId(),
                "calificacion", creada.getCalificacion(),
                "comentario", creada.getComentario(),
                "esCompraVerificada", creada.getEsCompraVerificada(),
                "fechaPublicacion", creada.getFechaPublicacion().toString()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody resenaRequestDTO dto) {
        resenaModel actualizada = resenaService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Resena con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "productoId", actualizada.getProductoId(),
                "usuarioId", actualizada.getUsuarioId(),
                "calificacion", actualizada.getCalificacion(),
                "comentario", actualizada.getComentario(),
                "esCompraVerificada", actualizada.getEsCompraVerificada(),
                "fechaPublicacion", actualizada.getFechaPublicacion().toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminar(id)));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", resenaService.eliminarTodos()));
    }
}