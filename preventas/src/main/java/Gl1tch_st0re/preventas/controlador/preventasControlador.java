package Gl1tch_st0re.preventas.controlador;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.servicio.preventasServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preventas")
public class preventasControlador {

    @Autowired
    private preventasServicio preventasServicio;

    // GET /api/preventas
    @GetMapping
    public ResponseEntity<List<preventasModelo>> listar() {
        List<preventasModelo> lista = preventasServicio.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/preventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(preventasServicio.findById(id));
    }

    // GET /api/preventas/usuario/{usuario}
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<preventasModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(preventasServicio.findByUsuario(usuario));
    }

    // GET /api/preventas/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<preventasModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(preventasServicio.findByEstado(estado));
    }

    // POST /api/preventas
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody preventasRequestDTO dto) {
        preventasModelo creada = preventasServicio.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Preventa creada correctamente",
                "id", creada.getId(),
                "usuario", creada.getUsuario(),
                "producto", creada.getProducto(),
                "cantidad", creada.getCantidad(),
                "estado", creada.getEstado(),
                "fechaReserva", creada.getFechaReserva().toString(),
                "fechaLanzamiento", creada.getFechaLanzamiento().toString()
        ));
    }

    // PUT /api/preventas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody preventasRequestDTO dto) {
        preventasModelo actualizada = preventasServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Preventa con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "usuario", actualizada.getUsuario(),
                "producto", actualizada.getProducto(),
                "cantidad", actualizada.getCantidad(),
                "estado", actualizada.getEstado(),
                "fechaReserva", actualizada.getFechaReserva().toString(),
                "fechaLanzamiento", actualizada.getFechaLanzamiento().toString()
        ));
    }

    // DELETE /api/preventas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = preventasServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    // DELETE /api/preventas
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = preventasServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}