package Gl1tch_st0re.pagos.controller;

import Gl1tch_st0re.pagos.dto.request.pagoRequestDTO;
import Gl1tch_st0re.pagos.model.pagoModel;
import Gl1tch_st0re.pagos.service.pagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class pagoController {

    @Autowired
    private pagoService pagoService;

    @GetMapping
    public ResponseEntity<List<pagoModel>> listar() {
        List<pagoModel> lista = pagoService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody pagoRequestDTO dto) {
        pagoModel creado = pagoService.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Pago creado correctamente",
                "id", creado.getId(),
                "ordenId", creado.getOrdenId(),
                "idTransaccionExterna", creado.getIdTransaccionExterna(),
                "metodoPago", creado.getMetodoPago(),
                "montoPagado", creado.getMontoPagado(),
                "estadoPago", creado.getEstadoPago()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody pagoRequestDTO dto) {
        pagoModel actualizado = pagoService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Pago con id " + id + " actualizado correctamente",
                "id", actualizado.getId(),
                "ordenId", actualizado.getOrdenId(),
                "idTransaccionExterna", actualizado.getIdTransaccionExterna(),
                "metodoPago", actualizado.getMetodoPago(),
                "montoPagado", actualizado.getMontoPagado(),
                "estadoPago", actualizado.getEstadoPago()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", pagoService.eliminar(id)));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", pagoService.eliminarTodos()));
    }
}