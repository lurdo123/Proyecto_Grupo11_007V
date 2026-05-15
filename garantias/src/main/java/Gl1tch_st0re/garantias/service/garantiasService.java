package Gl1tch_st0re.garantias.service;

import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.exceptions.garantiasNotFoundException;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.repository.garantiasRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class garantiasService {

    @Autowired
    private garantiasRepository garantiasRepository;

    public List<garantiasModel> findAll() {
        return garantiasRepository.findAll();
    }

    public garantiasModel obtenerPorId(Long id) {
        return garantiasRepository.findById(id)
                .orElseThrow(() -> new garantiasNotFoundException("Garantía con id " + id + " no encontrada"));
    }

    public garantiasModel crear(garantiasRequestDTO dto) {
        garantiasModel garantia = garantiasModel.builder()
                .productoId(dto.getProductoId())
                .ordenId(dto.getOrdenId())
                .mesesCobertura(dto.getMesesCobertura())
                .fechaVencimiento(dto.getFechaVencimiento())
                .build();
        return garantiasRepository.save(garantia);
    }

    public garantiasModel actualizar(Long id, garantiasRequestDTO dto) {
        garantiasModel garantia = garantiasRepository.findById(id)
                .orElseThrow(() -> new garantiasNotFoundException("Garantía con id " + id + " no encontrada"));

        garantia.setProductoId(dto.getProductoId());
        garantia.setOrdenId(dto.getOrdenId());
        garantia.setMesesCobertura(dto.getMesesCobertura());
        garantia.setFechaVencimiento(dto.getFechaVencimiento());

        return garantiasRepository.save(garantia);
    }

    public void eliminar(Long id) {
        garantiasModel garantia = garantiasRepository.findById(id)
                .orElseThrow(() -> new garantiasNotFoundException("Garantía con id " + id + " no encontrada"));
        garantiasRepository.delete(garantia);
    }

    public void eliminarTodos() {
        garantiasRepository.deleteAll();
    }
}