package Gl1tch_st0re.autenticacion.service;

import Gl1tch_st0re.autenticacion.dto.request.actualizarUsuarioRequestDTO;
import Gl1tch_st0re.autenticacion.dto.request.autenticacionRequestDTO;
import Gl1tch_st0re.autenticacion.exceptions.AutenticacionNotFoundException;
import Gl1tch_st0re.autenticacion.model.autenticacionModel;
import Gl1tch_st0re.autenticacion.repository.autenticacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class autenticacionService {
    @Autowired
    private autenticacionRepository autenticacionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<autenticacionModel> findAll() {
        return autenticacionRepository.findAll();
    }

    public boolean validarCredenciales(String usuario, String password) {
        Optional<autenticacionModel> clienteOpt = autenticacionRepository.findByUsuario(usuario);

        if (clienteOpt.isEmpty()) {
            return false;
        }

        return passwordEncoder.matches(password, clienteOpt.get().getPassword());
    }

    public autenticacionModel actualizarUsuario(Long id, actualizarUsuarioRequestDTO dto) {
        autenticacionModel usuario = autenticacionRepository.findById(id)
                .orElseThrow(() -> new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado"));

        usuario.setUsuario(dto.getUsuario());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        return autenticacionRepository.save(usuario);
    }

    public autenticacionModel crearUsuario(autenticacionRequestDTO dto) {
        autenticacionModel nuevoUsuario = autenticacionModel.builder()
                .usuario(dto.getUsuario())
                .password(passwordEncoder.encode(dto.getPassword()))
                .correo(dto.getCorreo())
                .build();

        autenticacionRepository.save(nuevoUsuario);
        autenticacionRepository.flush();
        return autenticacionRepository.findById(nuevoUsuario.getId()).orElseThrow();
    }

    public void eliminarUsuario(Long id) {
        autenticacionModel usuario = autenticacionRepository.findById(id)
                .orElseThrow(() -> new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado"));
        autenticacionRepository.delete(usuario);
    }

    public void eliminarTodos() {
        autenticacionRepository.deleteAll();
    }

    public autenticacionModel obtenerPorId(Long id) {
        return autenticacionRepository.findById(id)
                .orElseThrow(() -> new AutenticacionNotFoundException("Usuario con id " + id + " no encontrado"));
    }
}
