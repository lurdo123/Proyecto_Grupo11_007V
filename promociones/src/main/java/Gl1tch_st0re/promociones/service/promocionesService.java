package Gl1tch_st0re.promociones.service;

import Gl1tch_st0re.promociones.dto.request.promocionesRequestDTO;
import Gl1tch_st0re.promociones.exceptions.promocionesNotFoundException;
import Gl1tch_st0re.promociones.model.promocionesModel;
import Gl1tch_st0re.promociones.repository.promocionesRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class promocionesService {

    @Autowired
    private promocionesRepository promocionesRepository;

    public List<promocionesModel> findAll() {
        log.info("[promociones] Listando todas las promociones");
        return promocionesRepository.findAll();
    }

    public promocionesModel obtenerPorId(Long id) {
        log.info("[promociones] Buscando promoción con id {}", id);
        return promocionesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[promociones] Promoción con id {} no encontrada", id);
                    return new promocionesNotFoundException("Promoción con id " + id + " no encontrada");
                });
    }

    public promocionesModel crear(promocionesRequestDTO dto) {
        log.info("[promociones] Creando promoción con código '{}'", dto.getCodigo());
        promocionesModel promocion = promocionesModel.builder()
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .descuentoPorcentaje(dto.getDescuentoPorcentaje())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activo(dto.getActivo())
                .build();
        promocionesModel guardada = promocionesRepository.save(promocion);
        log.info("[promociones] Promoción creada con id {}", guardada.getId());
        return guardada;
    }

    public promocionesModel actualizar(Long id, promocionesRequestDTO dto) {
        log.info("[promociones] Actualizando promoción con id {}", id);
        promocionesModel promocion = promocionesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[promociones] Promoción con id {} no encontrada para actualizar", id);
                    return new promocionesNotFoundException("Promoción con id " + id + " no encontrada");
                });
        promocion.setCodigo(dto.getCodigo());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
        promocion.setActivo(dto.getActivo());
        promocionesModel actualizada = promocionesRepository.save(promocion);
        log.info("[promociones] Promoción con id {} actualizada correctamente", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        log.info("[promociones] Eliminando promoción con id {}", id);
        promocionesModel promocion = promocionesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[promociones] Promoción con id {} no encontrada para eliminar", id);
                    return new promocionesNotFoundException("Promoción con id " + id + " no encontrada");
                });
        promocionesRepository.delete(promocion);
        log.info("[promociones] Promoción con id {} eliminada correctamente", id);
    }

    public void eliminarTodos() {
        long total = promocionesRepository.count();
        log.warn("[promociones] Eliminando todas las promociones. Total: {}", total);
        promocionesRepository.deleteAll();
        log.info("[promociones] Todas las promociones eliminadas correctamente");
    }
}