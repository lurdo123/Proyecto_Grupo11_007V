package Gl1tch_st0re.ordenes.controlador;

import jakarta.servlet.http.HttpServletRequest;
import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.servicio.ordenesServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class ordenesControlador {

    @Autowired
    private ordenesServicio ordenesServicio;

    // GET /api/ordenes
    @GetMapping
    public ResponseEntity<List<ordenesModelo>> listar() {
        List<ordenesModelo> lista = ordenesServicio.findAll();
        if (lista.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/ordenes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenesServicio.findById(id));
    }

    // GET /api/ordenes/usuario/{usuario}
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(ordenesServicio.findByUsuario(usuario));
    }

    // GET /api/ordenes/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ordenesModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenesServicio.findByEstado(estado));
    }

    // POST /api/ordenes
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ordenesRequestDTO dto,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        ordenesModelo creada = ordenesServicio.crear(dto, token);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Orden creada correctamente",
                "id", creada.getId(),
                "usuario", creada.getUsuario(),
                "producto", creada.getProducto(),
                "cantidad", creada.getCantidad(),
                "estado", creada.getEstado(),
                "fechaCreacion", creada.getFechaCreacion().toString()));
    }

    // PUT /api/ordenes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
            @Valid @RequestBody ordenesRequestDTO dto) {
        ordenesModelo actualizada = ordenesServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Orden con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "usuario", actualizada.getUsuario(),
                "producto", actualizada.getProducto(),
                "cantidad", actualizada.getCantidad(),
                "estado", actualizada.getEstado(),
                "fechaCreacion", actualizada.getFechaCreacion().toString()));
    }

    // DELETE /api/ordenes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = ordenesServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    // DELETE /api/ordenes
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = ordenesServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}