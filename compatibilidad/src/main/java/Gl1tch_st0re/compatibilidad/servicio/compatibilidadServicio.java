package Gl1tch_st0re.compatibilidad.servicio;

import Gl1tch_st0re.compatibilidad.dto.request.compatibilidadRequestDTO;
import Gl1tch_st0re.compatibilidad.exceptions.compatibilidadNotFoundException;
import Gl1tch_st0re.compatibilidad.modelo.compatibilidadModelo;
import Gl1tch_st0re.compatibilidad.repositorio.compatibilidadRepositorio;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class compatibilidadServicio {

    @Autowired
    private compatibilidadRepositorio compatibilidadRepositorio;

    public List<compatibilidadModelo> findAll() {
        log.info("[compatibilidad] Listando todos los registros");
        return compatibilidadRepositorio.findAll();
    }

    public compatibilidadModelo findById(Long id) {
        log.info("[compatibilidad] Buscando compatibilidad con id {}", id);
        return compatibilidadRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("[compatibilidad] Compatibilidad con id {} no encontrada", id);
                    return new compatibilidadNotFoundException("Compatibilidad con id " + id + " no encontrada");
                });
    }

    public compatibilidadModelo crear(compatibilidadRequestDTO dto) {
        log.info("[compatibilidad] Creando compatibilidad entre '{}' y '{}'", dto.getComponenteBase(), dto.getComponenteCompatible());
        boolean existe = compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(
                dto.getComponenteBase(), dto.getComponenteCompatible());
        if (existe) {
            log.warn("[compatibilidad] Ya existe compatibilidad entre '{}' y '{}'", dto.getComponenteBase(), dto.getComponenteCompatible());
            throw new RuntimeException("Ya existe una compatibilidad entre '" + dto.getComponenteBase() + "' y '" + dto.getComponenteCompatible() + "'");
        }
        compatibilidadModelo nuevo = compatibilidadModelo.builder()
                .componenteBase(dto.getComponenteBase())
                .componenteCompatible(dto.getComponenteCompatible())
                .tipo(dto.getTipo())
                .build();
        compatibilidadModelo guardado = compatibilidadRepositorio.save(nuevo);
        log.info("[compatibilidad] Compatibilidad creada con id {}", guardado.getId());
        return guardado;
    }

    public compatibilidadModelo actualizar(Long id, compatibilidadRequestDTO dto) {
        log.info("[compatibilidad] Actualizando compatibilidad con id {}", id);
        compatibilidadModelo existente = findById(id);
        boolean existe = compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatibleAndIdNot(
                dto.getComponenteBase(), dto.getComponenteCompatible(), id);
        if (existe) {
            log.warn("[compatibilidad] Ya existe otra compatibilidad entre '{}' y '{}'", dto.getComponenteBase(), dto.getComponenteCompatible());
            throw new RuntimeException("Ya existe otra compatibilidad entre '" + dto.getComponenteBase() + "' y '" + dto.getComponenteCompatible() + "'");
        }
        existente.setComponenteBase(dto.getComponenteBase());
        existente.setComponenteCompatible(dto.getComponenteCompatible());
        existente.setTipo(dto.getTipo());
        compatibilidadModelo actualizado = compatibilidadRepositorio.save(existente);
        log.info("[compatibilidad] Compatibilidad con id {} actualizada correctamente", id);
        return actualizado;
    }

    public String eliminar(Long id) {
        log.info("[compatibilidad] Eliminando compatibilidad con id {}", id);
        compatibilidadModelo existente = findById(id);
        compatibilidadRepositorio.delete(existente);
        log.info("[compatibilidad] Compatibilidad con id {} eliminada correctamente", id);
        return "Compatibilidad con id " + id + " eliminada | " + existente.getComponenteBase() + " ↔ " + existente.getComponenteCompatible() + " | Tipo: " + existente.getTipo();
    }

    public String eliminarTodos() {
        long total = compatibilidadRepositorio.count();
        log.warn("[compatibilidad] Eliminando todos los registros. Total: {}", total);
        compatibilidadRepositorio.deleteAll();
        log.info("[compatibilidad] Todos los registros eliminados correctamente");
        return "Se eliminaron " + total + " registros de compatibilidad correctamente";
    }

    public boolean verificarCompatibilidad(String componenteBase, String componenteCompatible) {
        log.info("[compatibilidad] Verificando compatibilidad entre '{}' y '{}'", componenteBase, componenteCompatible);
        return compatibilidadRepositorio.existsByComponenteBaseAndComponenteCompatible(componenteBase, componenteCompatible);
    }
}