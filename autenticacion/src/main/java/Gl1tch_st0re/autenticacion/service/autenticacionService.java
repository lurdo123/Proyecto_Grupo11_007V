package Gl1tch_st0re.autenticacion.service;

import Gl1tch_st0re.autenticacion.dto.request.actualizarUsuarioRequestDTO;
import Gl1tch_st0re.autenticacion.dto.request.autenticacionRequestDTO;
import Gl1tch_st0re.autenticacion.exceptions.AutenticacionNotFoundException;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import Gl1tch_st0re.autenticacion.repository.autenticacionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class autenticacionService {

    @Autowired
    private autenticacionRepository autenticacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<autenticacionModel> findAll() {
        log.info("[autenticacion] Listando todos los usuarios");
        return autenticacionRepository.findAll();
    }

    public autenticacionModel obtenerPorId(Long id) {
        log.info("[autenticacion] Buscando usuario con id {}", id);
        return autenticacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[autenticacion] Usuario con id {} no encontrado", id);
                    return new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado");
                });
    }

    public boolean validarCredenciales(String usuario, String password) {
        log.info("[autenticacion] Validando credenciales para usuario '{}'", usuario);
        Optional<autenticacionModel> clienteOpt = autenticacionRepository.findByUsuario(usuario);
        if (clienteOpt.isEmpty()) {
            log.warn("[autenticacion] Usuario '{}' no encontrado al validar credenciales", usuario);
            return false;
        }
        boolean valido = passwordEncoder.matches(password, clienteOpt.get().getPassword());
        log.info("[autenticacion] Resultado validación para '{}': {}", usuario, valido);
        return valido;
    }

    public autenticacionModel crearUsuario(autenticacionRequestDTO dto) {
        log.info("[autenticacion] Creando usuario '{}'", dto.getUsuario());
        autenticacionModel nuevoUsuario = autenticacionModel.builder()
                .usuario(dto.getUsuario())
                .password(passwordEncoder.encode(dto.getPassword()))
                .correo(dto.getCorreo())
                .build();
        autenticacionRepository.save(nuevoUsuario);
        autenticacionRepository.flush();
        autenticacionModel guardado = autenticacionRepository.findById(nuevoUsuario.getId()).orElseThrow();
        log.info("[autenticacion] Usuario '{}' creado con id {}", guardado.getUsuario(), guardado.getId());
        return guardado;
    }

    public autenticacionModel actualizarUsuario(Long id, actualizarUsuarioRequestDTO dto) {
        log.info("[autenticacion] Actualizando usuario con id {}", id);
        autenticacionModel usuario = autenticacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[autenticacion] Usuario con id {} no encontrado para actualizar", id);
                    return new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado");
                });
        usuario.setUsuario(dto.getUsuario());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        autenticacionModel actualizado = autenticacionRepository.save(usuario);
        log.info("[autenticacion] Usuario con id {} actualizado correctamente", id);
        return actualizado;
    }

    public void eliminarUsuario(Long id) {
        log.info("[autenticacion] Eliminando usuario con id {}", id);
        autenticacionModel usuario = autenticacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[autenticacion] Usuario con id {} no encontrado para eliminar", id);
                    return new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado");
                });
        autenticacionRepository.delete(usuario);
        log.info("[autenticacion] Usuario con id {} eliminado correctamente", id);
    }

    public void eliminarTodos() {
        long total = autenticacionRepository.count();
        log.warn("[autenticacion] Eliminando todos los usuarios. Total: {}", total);
        autenticacionRepository.deleteAll();
        log.info("[autenticacion] Todos los usuarios eliminados correctamente");
    }
}