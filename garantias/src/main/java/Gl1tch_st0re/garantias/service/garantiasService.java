package Gl1tch_st0re.garantias.service;

import Gl1tch_st0re.garantias.client.catalogoWebClient;
import Gl1tch_st0re.garantias.dto.request.garantiasRequestDTO;
import Gl1tch_st0re.garantias.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.garantias.exceptions.garantiasNotFoundException;
import Gl1tch_st0re.garantias.model.garantiasModel;
import Gl1tch_st0re.garantias.repository.garantiasRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class garantiasService {

    @Autowired
    private garantiasRepository garantiasRepository;

    @Autowired
    private catalogoWebClient catalogoWebClient;

    public List<garantiasModel> findAll() {
        log.info("[garantias] Listando todas las garantías");
        return garantiasRepository.findAll();
    }

    public garantiasModel obtenerPorId(Long id) {
        log.info("[garantias] Buscando garantía con id {}", id);
        return garantiasRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[garantias] Garantía con id {} no encontrada", id);
                    return new garantiasNotFoundException("Garantía con id " + id + " no encontrada");
                });
    }

    public garantiasModel crear(garantiasRequestDTO dto, String token) {
        log.info("[garantias] Validando producto_id {} en servicio catalogo", dto.getProductoId());
        try {
            catalogoClienteDTO producto = catalogoWebClient.obtenerProducto(dto.getProductoId(), token);
            if (producto == null) throw new RuntimeException();
            log.info("[garantias] Producto {} validado correctamente", dto.getProductoId());
        } catch (Exception e) {
            log.error("[garantias] Producto con id {} no encontrado en servicio catalogo", dto.getProductoId());
            throw new RuntimeException("Producto con id " + dto.getProductoId() + " no encontrado en catálogo");
        }
        garantiasModel garantia = garantiasModel.builder()
                .productoId(dto.getProductoId())
                .ordenId(dto.getOrdenId())
                .mesesCobertura(dto.getMesesCobertura())
                .fechaVencimiento(dto.getFechaVencimiento())
                .build();
        garantiasModel guardada = garantiasRepository.save(garantia);
        log.info("[garantias] Garantía creada con id {}", guardada.getId());
        return guardada;
    }

    public garantiasModel actualizar(Long id, garantiasRequestDTO dto) {
        log.info("[garantias] Actualizando garantía con id {}", id);
        garantiasModel garantia = garantiasRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[garantias] Garantía con id {} no encontrada para actualizar", id);
                    return new garantiasNotFoundException("Garantía con id " + id + " no encontrada");
                });
        garantia.setProductoId(dto.getProductoId());
        garantia.setOrdenId(dto.getOrdenId());
        garantia.setMesesCobertura(dto.getMesesCobertura());
        garantia.setFechaVencimiento(dto.getFechaVencimiento());
        garantiasModel actualizada = garantiasRepository.save(garantia);
        log.info("[garantias] Garantía con id {} actualizada correctamente", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        log.info("[garantias] Eliminando garantía con id {}", id);
        garantiasModel garantia = garantiasRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[garantias] Garantía con id {} no encontrada para eliminar", id);
                    return new garantiasNotFoundException("Garantía con id " + id + " no encontrada");
                });
        garantiasRepository.delete(garantia);
        log.info("[garantias] Garantía con id {} eliminada correctamente", id);
    }

    public void eliminarTodos() {
        long total = garantiasRepository.count();
        log.warn("[garantias] Eliminando todas las garantías. Total: {}", total);
        garantiasRepository.deleteAll();
        log.info("[garantias] Todas las garantías eliminadas correctamente");
    }
}