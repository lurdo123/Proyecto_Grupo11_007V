package Gl1tch_st0re.envios.controlador;

import Gl1tch_st0re.envios.dto.request.enviosRequestDTO;
import Gl1tch_st0re.envios.modelo.enviosModelo;
import Gl1tch_st0re.envios.servicio.enviosServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class enviosControlador {

    @Autowired
    private enviosServicio enviosServicio;

    // GET /api/envios
    @GetMapping
    public ResponseEntity<List<enviosModelo>> listar() {
        List<enviosModelo> lista = enviosServicio.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    // GET /api/envios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(enviosServicio.findById(id));
    }

    // GET /api/envios/usuario/{usuario}
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<List<enviosModelo>> obtenerPorUsuario(@PathVariable String usuario) {
        return ResponseEntity.ok(enviosServicio.findByUsuario(usuario));
    }

    // GET /api/envios/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<enviosModelo>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(enviosServicio.findByEstado(estado));
    }

    // GET /api/envios/orden/{ordenId}
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<enviosModelo>> obtenerPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(enviosServicio.findByOrdenId(ordenId));
    }

    // POST /api/envios
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody enviosRequestDTO dto) {
        enviosModelo creado = enviosServicio.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Envío creado correctamente",
                "id", creado.getId(),
                "ordenId", creado.getOrdenId(),
                "usuario", creado.getUsuario(),
                "direccion", creado.getDireccion(),
                "estado", creado.getEstado(),
                "transportista", creado.getTransportista(),
                "fechaEnvio", creado.getFechaEnvio().toString(),
                "fechaEntregaEstimada", creado.getFechaEntregaEstimada().toString()
        ));
    }

    // PUT /api/envios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody enviosRequestDTO dto) {
        enviosModelo actualizado = enviosServicio.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Envío con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "ordenId", actualizado.getOrdenId(),
                "usuario", actualizado.getUsuario(),
                "direccion", actualizado.getDireccion(),
                "estado", actualizado.getEstado(),
                "transportista", actualizado.getTransportista(),
                "fechaEnvio", actualizado.getFechaEnvio().toString(),
                "fechaEntregaEstimada", actualizado.getFechaEntregaEstimada().toString()
        ));
    }

    // DELETE /api/envios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        String mensaje = enviosServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }

    // DELETE /api/envios
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        String mensaje = enviosServicio.eliminarTodos();
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}