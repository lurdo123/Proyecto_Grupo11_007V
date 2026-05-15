package Gl1tch_st0re.autenticacion.service;
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
}
