package Gl1tch_st0re.compatibilidad.servicio;

import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.repositorio.compatibilidadRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class compatibilidadServicio {

    @Autowired
    public compatibilidadRepositorio compatibilidadRepositorio;

    public List<compatibilidadModelo> findAll() {
        return compatibilidadRepositorio.findAll();
    }

    public boolean validarCompatibilidad(String componenteBase, String componenteCompatible) {
        Optional<compatibilidadModelo> compOpt = compatibilidadRepositorio.findByComponenteBase(componenteBase);
        
        if (compOpt.isEmpty()) {
            return false;
        }

        // Aquí comparamos si el componente compatible coincide, 
        // reemplazando la lógica del PasswordEncoder que tenías en el login
        return compOpt.get().getComponenteCompatible().equals(componenteCompatible);
    }
}