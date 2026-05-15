package Gl1tch_st0re.inventario.service;

import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.repository.inventarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class inventarioService {
    @Autowired
    private inventarioRepository inventarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<inventarioModel> findAll() {
        return inventarioRepository.findAll();
    }

    public boolean validarCredenciales(String usuario, String password) {
        Optional<inventarioModel> clienteOpt = inventarioRepository.findByUsuario(usuario);

        if (clienteOpt.isEmpty()) {
            return false;
        }

        return passwordEncoder.matches(password, clienteOpt.get().getPassword());
    }
}
