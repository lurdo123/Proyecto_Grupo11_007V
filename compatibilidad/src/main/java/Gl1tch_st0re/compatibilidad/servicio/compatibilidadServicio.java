package Gl1tch_st0re.compatibilidad.servicio;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.exceptions.compatibilidadNotFoundException;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.repositorio.compatibilidadRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class compatibilidadServicio {

    @Autowired
    private compatibilidadRepositorio compatibilidadRepositorio;

    public List<compatibilidadModelo> findAll() {
        return compatibilidadRepositorio.findAll();
    }

    public compatibilidadModelo findById(Long id) {
        return compatibilidadRepositorio.findById(id)
                .orElseThrow(() -> new compatibilidadNotFoundException(
                        "Compatibilidad con id " + id + " no encontrada"));
    }

    public compatibilidadModelo crear(compatibilidadRequestDTO dto) {
        boolean existe = compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                dto.getComponenteBase(), dto.getComponenteCompatible());
        if (existe) {
            throw new RuntimeException("Ya existe una compatibilidad entre '"
                    + dto.getComponenteBase() + "' y '" + dto.getComponenteCompatible() + "'");
        }
        compatibilidadModelo nuevo = compatibilidadModelo.builder()
                .componenteBase(dto.getComponenteBase())
                .componenteCompatible(dto.getComponenteCompatible())
                .tipo(dto.getTipo())
                .build();
        return compatibilidadRepositorio.save(nuevo);
    }

    public compatibilidadModelo actualizar(Long id, compatibilidadRequestDTO dto) {
        compatibilidadModelo existente = findById(id);

        boolean existe = compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatibleAndIdNot(
                dto.getComponenteBase(), dto.getComponenteCompatible(), id);
        if (existe) {
            throw new RuntimeException("Ya existe otra compatibilidad entre '"
                    + dto.getComponenteBase() + "' y '" + dto.getComponenteCompatible() + "'");
        }

        existente.setComponenteBase(dto.getComponenteBase());
        existente.setComponenteCompatible(dto.getComponenteCompatible());
        existente.setTipo(dto.getTipo());
        return compatibilidadRepositorio.save(existente);
    }

    public String eliminar(Long id) {
        compatibilidadModelo existente = findById(id);
        compatibilidadRepositorio.delete(existente);
        return "Compatibilidad con id " + id + " eliminada | "
                + existente.getComponenteBase() + " ↔ " + existente.getComponenteCompatible()
                + " | Tipo: " + existente.getTipo();
    }

    public String eliminarTodos() {
        long total = compatibilidadRepositorio.count();
        compatibilidadRepositorio.deleteAll();
        return "Se eliminaron " + total + " registros de compatibilidad correctamente";
    }

    public boolean verificarCompatibilidad(String componenteBase, String componenteCompatible) {
        return compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                componenteBase, componenteCompatible);
    }
}