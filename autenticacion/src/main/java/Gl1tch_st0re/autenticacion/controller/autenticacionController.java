package Gl1tch_st0re.autenticacion.controller;

import Gl1tch_st0re.autenticacion.dto.request.actualizarUsuarioRequestDTO;
import Gl1tch_st0re.autenticacion.dto.request.autenticacionRequestDTO;
import jakarta.validation.Valid;
import Gl1tch_st0re.autenticacion.dto.request.loginRequest;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import Gl1tch_st0re.autenticacion.security.JwtService;
import Gl1tch_st0re.autenticacion.service.autenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autenticaciones")
@Tag(name = "Autenticación", description = "Registro de usuarios, login y generación de tokens JWT")
public class autenticacionController {

    @Autowired
    private autenticacionService autenticacionService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios registrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios"),
            @ApiResponse(responseCode = "204", description = "Sin usuarios registrados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<autenticacionModel>> listar() {
        List<autenticacionModel> autenticaciones = autenticacionService.findAll();
        if (autenticaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(autenticaciones);
    }

    @Operation(summary = "Login", description = "Valida credenciales y retorna un token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequest request) {
        boolean credencialesValidas = autenticacionService.validarCredenciales(
                request.getUsuario(),
                request.getPassword());

        if (!credencialesValidas) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtService.generarToken(request.getUsuario());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Generar hash", description = "Genera el hash BCrypt de un texto (uso interno/testing)")
    @ApiResponse(responseCode = "200", description = "Hash generado exitosamente")
    @GetMapping("/hash")
    public String generarHash(@RequestParam String texto) {
        return passwordEncoder.encode(texto);
    }

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        autenticacionModel usuario = autenticacionService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario con contraseña encriptada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody autenticacionRequestDTO dto) {
        autenticacionModel creado = autenticacionService.crearUsuario(dto);
        return ResponseEntity.status(201).body(creado);
    }

    @Operation(summary = "Actualizar usuario", description = "Modifica usuario y re-encripta la contraseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody actualizarUsuarioRequestDTO dto) {
        autenticacionModel usuarioActualizado = autenticacionService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @Operation(summary = "Eliminar usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        autenticacionService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar todos los usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todos los usuarios eliminados"),
            @ApiResponse(responseCode = "401", description = "Token JWT requerido")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> eliminarTodos() {
        autenticacionService.eliminarTodos();
        return ResponseEntity.noContent().build();
    }
}
