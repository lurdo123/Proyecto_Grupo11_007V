package Gl1tch_st0re.clientes.controller;

import Gl1tch_st0re.clientes.dto.request.direccionClienteRequestDTO;
import Gl1tch_st0re.clientes.model.direccionClienteModel;
import Gl1tch_st0re.clientes.service.direccionClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes/direcciones")
public class direccionClienteController {

    @Autowired
    private direccionClienteService direccionClienteService;

    @GetMapping
    public ResponseEntity<List<direccionClienteModel>> listar() {
        List<direccionClienteModel> lista = direccionClienteService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(direccionClienteService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long clienteId) {
        List<direccionClienteModel> lista = direccionClienteService.findByClienteId(clienteId);
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody direccionClienteRequestDTO dto) {
        direccionClienteModel creada = direccionClienteService.crear(dto);
        return ResponseEntity.status(201).body(Map.of(
                "mensaje", "Direccion creada correctamente",
                "id", creada.getId(),
                "clienteId", creada.getClienteId(),
                "tipoDireccion", creada.getTipoDireccion(),
                "calleNumero", creada.getCalleNumero(),
                "comunaCiudad", creada.getComunaCiudad(),
                "regionEstado", creada.getRegionEstado(),
                "esPrincipal", creada.getEsPrincipal()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody direccionClienteRequestDTO dto) {
        direccionClienteModel actualizada = direccionClienteService.actualizar(id, dto);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Direccion con id " + id + " actualizada correctamente",
                "id", actualizada.getId(),
                "clienteId", actualizada.getClienteId(),
                "tipoDireccion", actualizada.getTipoDireccion(),
                "calleNumero", actualizada.getCalleNumero(),
                "comunaCiudad", actualizada.getComunaCiudad(),
                "regionEstado", actualizada.getRegionEstado(),
                "esPrincipal", actualizada.getEsPrincipal()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("mensaje", direccionClienteService.eliminar(id)));
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        return ResponseEntity.ok(Map.of("mensaje", direccionClienteService.eliminarTodos()));
    }
}