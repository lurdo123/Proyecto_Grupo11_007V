package Gl1tch_st0re.autenticacion.controller;

import Gl1tch_st0re.autenticacion.dto.request.actualizarUsuarioRequestDTO;
import Gl1tch_st0re.autenticacion.dto.request.autenticacionRequestDTO;
import jakarta.validation.Valid;
import Gl1tch_st0re.autenticacion.dto.request.loginRequest;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import Gl1tch_st0re.autenticacion.security.JwtService;
import Gl1tch_st0re.autenticacion.service.autenticacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autenticaciones")

public class autenticacionController {
    @Autowired
    private autenticacionService autenticacionService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<autenticacionModel>> listar() {
        List<autenticacionModel> autenticaciones = autenticacionService.findAll();
        if (autenticaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(autenticaciones);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request) {
        // Busca el usuario en la BD
        boolean credencialesValidas = autenticacionService.validarCredenciales(
                request.getUsuario(),
                request.getPassword());

        if (!credencialesValidas) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generarToken(request.getUsuario());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hash")
    public String generarHash(@RequestParam String texto) {
        return passwordEncoder.encode(texto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody actualizarUsuarioRequestDTO dto) {

        autenticacionModel usuarioActualizado = autenticacionService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody autenticacionRequestDTO dto) {
        autenticacionModel creado = autenticacionService.crearUsuario(dto);
        return ResponseEntity.status(201).body(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        autenticacionService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        autenticacionService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        autenticacionModel usuario = autenticacionService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }
}
