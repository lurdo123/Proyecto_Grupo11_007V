package Gl1tch_st0re.inventario.service;

import Gl1tch_st0re.inventario.client.catalogoWebClient;
import Gl1tch_st0re.inventario.dto.request.inventarioRequestDTO;
import Gl1tch_st0re.inventario.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.inventario.exceptions.inventarioNotFoundException;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.repository.inventarioRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class inventarioService {

    @Autowired
    private inventarioRepository inventarioRepository;

    @Autowired
    private catalogoWebClient catalogoWebClient;

    public List<inventarioModel> findAll() {
        log.info("[inventario] Listando todos los registros");
        return inventarioRepository.findAll();
    }

    public inventarioModel obtenerPorId(Long id) {
        log.info("[inventario] Buscando inventario con id {}", id);
        return inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[inventario] Inventario con id {} no encontrado", id);
                    return new inventarioNotFoundException("Inventario con id " + id + " no encontrado");
                });
    }

    public inventarioModel crear(inventarioRequestDTO dto, String token) {
        log.info("[inventario] Validando producto_id {} en servicio catalogo", dto.getProductoId());
        try {
            catalogoClienteDTO producto = catalogoWebClient.obtenerProducto(dto.getProductoId(), token);
            if (producto == null) throw new RuntimeException();
            log.info("[inventario] Producto {} validado correctamente", dto.getProductoId());
        } catch (Exception e) {
            log.error("[inventario] Producto con id {} no encontrado en servicio catalogo", dto.getProductoId());
            throw new RuntimeException("Producto con id " + dto.getProductoId() + " no encontrado en catálogo");
        }
        boolean existe = inventarioRepository.existsByProductoId(dto.getProductoId());
        if (existe) {
            log.warn("[inventario] Ya existe inventario para producto_id {}", dto.getProductoId());
            throw new RuntimeException("Ya existe un inventario con el productoId " + dto.getProductoId());
        }
        inventarioModel inv = inventarioModel.builder()
                .productoId(dto.getProductoId())
                .estadoFisico(dto.getEstadoFisico())
                .cantidadDisponible(dto.getCantidadDisponible())
                .ubicacionBodega(dto.getUbicacionBodega())
                .build();
        inventarioModel guardado = inventarioRepository.save(inv);
        log.info("[inventario] Inventario creado con id {}", guardado.getId());
        return guardado;
    }

    public inventarioModel actualizar(Long id, inventarioRequestDTO dto) {
        log.info("[inventario] Actualizando inventario con id {}", id);
        inventarioModel inv = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[inventario] Inventario con id {} no encontrado para actualizar", id);
                    return new inventarioNotFoundException("Inventario con id " + id + " no encontrado");
                });
        boolean existe = inventarioRepository.existsByProductoIdAndIdNot(dto.getProductoId(), id);
        if (existe) {
            log.warn("[inventario] Ya existe otro inventario con producto_id {}", dto.getProductoId());
            throw new RuntimeException("Ya existe otro inventario con el productoId " + dto.getProductoId());
        }
        inv.setProductoId(dto.getProductoId());
        inv.setEstadoFisico(dto.getEstadoFisico());
        inv.setCantidadDisponible(dto.getCantidadDisponible());
        inv.setUbicacionBodega(dto.getUbicacionBodega());
        inventarioModel actualizado = inventarioRepository.save(inv);
        log.info("[inventario] Inventario con id {} actualizado correctamente", id);
        return actualizado;
    }

    public String eliminar(Long id) {
        log.info("[inventario] Eliminando inventario con id {}", id);
        inventarioModel inv = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[inventario] Inventario con id {} no encontrado para eliminar", id);
                    return new inventarioNotFoundException("Inventario con id " + id + " no encontrado");
                });
        inventarioRepository.delete(inv);
        log.info("[inventario] Inventario con id {} eliminado correctamente", id);
        return "Inventario con id " + id + " eliminado | Producto ID: " + inv.getProductoId() + " | Estado: " + inv.getEstadoFisico() + " | Bodega: " + inv.getUbicacionBodega();
    }

    public String eliminarTodos() {
        long total = inventarioRepository.count();
        log.warn("[inventario] Eliminando todos los registros. Total: {}", total);
        inventarioRepository.deleteAll();
        log.info("[inventario] Todos los registros eliminados correctamente");
        return "Se eliminaron " + total + " registros de inventario correctamente";
    }
}