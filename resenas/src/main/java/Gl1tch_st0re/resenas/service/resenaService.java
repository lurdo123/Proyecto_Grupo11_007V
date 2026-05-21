package Gl1tch_st0re.resenas.service;

import Gl1tch_st0re.resenas.client.catalogoWebClient;
import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.resenas.exceptions.resenaNotFoundException;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.repository.resenaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class resenaService {

    @Autowired
    private resenaRepository resenaRepository;

    @Autowired
    private catalogoWebClient catalogoWebClient;

    public List<resenaModel> findAll() {
        log.info("[resenas] Listando todas las reseñas");
        return resenaRepository.findAll();
    }

    public resenaModel obtenerPorId(Long id) {
        log.info("[resenas] Buscando reseña con id {}", id);
        return resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[resenas] Reseña con id {} no encontrada", id);
                    return new resenaNotFoundException("Resena con id " + id + " no encontrada");
                });
    }

    public resenaModel crear(resenaRequestDTO dto, String token) {
        log.info("[resenas] Validando producto_id {} en servicio catalogo", dto.getProductoId());
        try {
            catalogoClienteDTO producto = catalogoWebClient.obtenerProducto(dto.getProductoId(), token);
            if (producto == null) throw new RuntimeException();
            log.info("[resenas] Producto {} validado correctamente", dto.getProductoId());
        } catch (Exception e) {
            log.error("[resenas] Producto con id {} no encontrado en servicio catalogo", dto.getProductoId());
            throw new RuntimeException("Producto con id " + dto.getProductoId() + " no encontrado en catálogo");
        }
        boolean existe = resenaRepository.existsByProductoIdAndUsuarioId(dto.getProductoId(), dto.getUsuarioId());
        if (existe) {
            log.warn("[resenas] Ya existe reseña del usuario {} para producto {}", dto.getUsuarioId(), dto.getProductoId());
            throw new RuntimeException("Ya existe una resena del usuario " + dto.getUsuarioId() + " para el producto " + dto.getProductoId());
        }
        resenaModel resena = resenaModel.builder()
                .productoId(dto.getProductoId())
                .usuarioId(dto.getUsuarioId())
                .calificacion(dto.getCalificacion())
                .comentario(dto.getComentario())
                .esCompraVerificada(dto.getEsCompraVerificada())
                .fechaPublicacion(dto.getFechaPublicacion())
                .build();
        resenaModel guardada = resenaRepository.save(resena);
        log.info("[resenas] Reseña creada con id {}", guardada.getId());
        return guardada;
    }

    public resenaModel actualizar(Long id, resenaRequestDTO dto) {
        log.info("[resenas] Actualizando reseña con id {}", id);
        resenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[resenas] Reseña con id {} no encontrada para actualizar", id);
                    return new resenaNotFoundException("Resena con id " + id + " no encontrada");
                });
        boolean existe = resenaRepository.existsByProductoIdAndUsuarioIdAndIdNot(dto.getProductoId(), dto.getUsuarioId(), id);
        if (existe) {
            log.warn("[resenas] Ya existe otra reseña del usuario {} para producto {}", dto.getUsuarioId(), dto.getProductoId());
            throw new RuntimeException("Ya existe otra resena del usuario " + dto.getUsuarioId() + " para el producto " + dto.getProductoId());
        }
        resena.setProductoId(dto.getProductoId());
        resena.setUsuarioId(dto.getUsuarioId());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setEsCompraVerificada(dto.getEsCompraVerificada());
        resena.setFechaPublicacion(dto.getFechaPublicacion());
        resenaModel actualizada = resenaRepository.save(resena);
        log.info("[resenas] Reseña con id {} actualizada correctamente", id);
        return actualizada;
    }

    public String eliminar(Long id) {
        log.info("[resenas] Eliminando reseña con id {}", id);
        resenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[resenas] Reseña con id {} no encontrada para eliminar", id);
                    return new resenaNotFoundException("Resena con id " + id + " no encontrada");
                });
        resenaRepository.delete(resena);
        log.info("[resenas] Reseña con id {} eliminada correctamente", id);
        return "Resena con id " + id + " eliminada | Producto ID: " + resena.getProductoId() + " | Usuario ID: " + resena.getUsuarioId() + " | Calificacion: " + resena.getCalificacion();
    }

    public String eliminarTodos() {
        long total = resenaRepository.count();
        log.warn("[resenas] Eliminando todas las reseñas. Total: {}", total);
        resenaRepository.deleteAll();
        log.info("[resenas] Todas las reseñas eliminadas correctamente");
        return "Se eliminaron " + total + " resenas correctamente";
    }
}