package Gl1tch_st0re.compatibilidad.controlador;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.dto.request.loginRequest;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.security.JwtService;
import Gl1tch_st0re.compatibilidad.servicio.compatibilidadServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compatibilidades")
public class compatibilidadControlador {

    @Autowired
    private compatibilidadServicio compatibilidadServicio;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<compatibilidadModelo>> listar() {
        List<compatibilidadModelo> compatibilidades = compatibilidadServicio.findAll();
        if (compatibilidades.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(compatibilidades);
    }

    @PostMapping("/verificar")
    public ResponseEntity<?> verificar(@RequestBody compatibilidadRequestDTO request) {
        // Valida la compatibilidad entre los componentes
        boolean esCompatible = compatibilidadServicio.validarCompatibilidad(                request.getComponenteBase(),
                request.getComponenteCompatible());

        if (!esCompatible) {
            return ResponseEntity.status(401).body(Map.of("error", "Los componentes no son compatibles"));
        }

        // Siguiendo tu estructura, generamos un token para la sesión de consulta
        String token = jwtService.generateToken(request.getComponenteBase());
        return ResponseEntity.ok(Map.of("token", token, "mensaje", "Compatibilidad confirmada"));
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody loginRequest request) {

    if(request.getUsuario().equals("vicente")
       && request.getPassword().equals("1234")) {

        String token = jwtService.generateToken(request.getUsuario());

        return ResponseEntity.ok(Map.of(
            "token", token,
            "mensaje", "Login correcto"
        ));
    }

    return ResponseEntity.status(401)
            .body(Map.of("error", "Credenciales inválidas"));
}

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hash")
    public String generarHash(@RequestParam String texto) {
        return passwordEncoder.encode(texto);
    }
}