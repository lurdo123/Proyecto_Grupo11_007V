package Gl1tch_st0re.promociones.service;

import Gl1tch_st0re.promociones.dto.request.promocionesRequestDTO;
import Gl1tch_st0re.promociones.exceptions.promocionesNotFoundException;
import Gl1tch_st0re.promociones.model.promocionesModel;
import Gl1tch_st0re.promociones.repository.promocionesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class promocionesService {

    @Autowired
    private promocionesRepository promocionesRepository;

    public List<promocionesModel> findAll() {
        return promocionesRepository.findAll();
    }

    public promocionesModel obtenerPorId(Long id) {
        return promocionesRepository.findById(id)
                .orElseThrow(() -> new promocionesNotFoundException("Promoción con id " + id + " no encontrada"));
    }

    public promocionesModel crear(promocionesRequestDTO dto) {
        promocionesModel promocion = promocionesModel.builder()
                .codigo(dto.getCodigo())
                .descripcion(dto.getDescripcion())
                .descuentoPorcentaje(dto.getDescuentoPorcentaje())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activo(dto.getActivo())
                .build();
        return promocionesRepository.save(promocion);
    }

    public promocionesModel actualizar(Long id, promocionesRequestDTO dto) {
        promocionesModel promocion = promocionesRepository.findById(id)
                .orElseThrow(() -> new promocionesNotFoundException("Promoción con id " + id + " no encontrada"));

        promocion.setCodigo(dto.getCodigo());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setDescuentoPorcentaje(dto.getDescuentoPorcentaje());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
        promocion.setActivo(dto.getActivo());

        return promocionesRepository.save(promocion);
    }

    public void eliminar(Long id) {
        promocionesModel promocion = promocionesRepository.findById(id)
                .orElseThrow(() -> new promocionesNotFoundException("Promoción con id " + id + " no encontrada"));
        promocionesRepository.delete(promocion);
    }

    public void eliminarTodos() {
        promocionesRepository.deleteAll();
    }
}