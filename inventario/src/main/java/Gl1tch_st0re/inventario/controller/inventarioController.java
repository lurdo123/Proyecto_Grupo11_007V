package Gl1tch_st0re.inventario.controller;

import Gl1tch_st0re.inventario.dto.request.loginRequest;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.security.JwtService;
import Gl1tch_st0re.inventario.service.inventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")

public class inventarioController {

    @Autowired
    private inventarioService inventarioServicio;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<inventarioModel>> listar() {

        List<inventarioModel> inventarios = inventarioServicio.findAll();

        if (inventarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(inventarios);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request) {

        boolean credencialesValidas = inventarioServicio.validarCredenciales(
                request.getUsuario(),
                request.getPassword());

        if (!credencialesValidas) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generarToken(request.getUsuario());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/hash")
    public String generarHash(@RequestParam String texto) {
        return passwordEncoder.encode(texto);
    }
}