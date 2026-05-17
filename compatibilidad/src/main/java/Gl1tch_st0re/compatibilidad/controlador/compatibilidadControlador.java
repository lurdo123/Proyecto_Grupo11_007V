package Gl1tch_st0re.compatibilidad.controlador;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.servicio.compatibilidadServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compatibilidades")
public class compatibilidadControlador {

    @Autowired
    private compatibilidadServicio compatibilidadServicio;

    // GET /api/compatibilidades
    @GetMapping
    public ResponseEntity<List<compatibilidadModelo>> listar() {
        List<compatibilidadModelo> lista = compatibilidadServicio.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/compatibilidades/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compatibilidadServicio.findById(id));
    }

    // POST /api/compatibilidades
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody compatibilidadRequestDTO dto) {
        compatibilidadModelo creado = compatibilidadServicio.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Compatibilidad creada correctamente",
                "id", creado.getId(),
                "componenteBase", creado.getComponenteBase(),
                "componenteCompatible", creado.getComponenteCompatible(),
                "tipo", creado.getTipo()
        ));
    }

    // PUT /api/compatibilidades/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody compatibilidadRequestDTO dto) {
        compatibilidadModelo actualizado = compatibilidadServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Compatibilidad con id " + id + " actualizada correctamente",
                "id", actualizado.getId(),
                "componenteBase", actualizado.getComponenteBase(),
                "componenteCompatible", actualizado.getComponenteCompatible(),
                "tipo", actualizado.getTipo()
        ));
    }

    // DELETE /api/compatibilidades/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = compatibilidadServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    // DELETE /api/compatibilidades
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = compatibilidadServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    // GET /api/compatibilidades/verificar?componenteBase=X&componenteCompatible=Y
    @GetMapping("/verificar")
    public ResponseEntity<?> verificar(
            @RequestParam String componenteBase,
            @RequestParam String componenteCompatible) {
        boolean compatible = compatibilidadServicio.verificarCompatibilidad(componenteBase, componenteCompatible);
        return ResponseEntity.ok(Map.of(
                "componenteBase", componenteBase,
                "componenteCompatible", componenteCompatible,
                "esCompatible", compatible
        ));
    }
}