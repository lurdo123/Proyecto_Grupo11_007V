package Gl1tch_st0re.ordenes.servicio;

import Gl1tch_st0re.ordenes.client.catalogoWebClient;
import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.dto.response.catalogoClienteDTO;
import Gl1tch_st0re.ordenes.exceptions.ordenesNotFoundException;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.repositorio.ordenesRepositorio;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ordenesServicio {

    @Autowired
    private ordenesRepositorio ordenesRepositorio;

    @Autowired
    private catalogoWebClient catalogoWebClient;

    public List<ordenesModelo> findAll() {
        log.info("[ordenes] Listando todas las órdenes");
        return ordenesRepositorio.findAll();
    }

    public ordenesModelo findById(Long id) {
        log.info("[ordenes] Buscando orden con id {}", id);
        return ordenesRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("[ordenes] Orden con id {} no encontrada", id);
                    return new ordenesNotFoundException("Orden con id " + id + " no encontrada");
                });
    }

    public List<ordenesModelo> findByUsuario(String usuario) {
        log.info("[ordenes] Buscando órdenes para usuario '{}'", usuario);
        List<ordenesModelo> lista = ordenesRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            log.warn("[ordenes] No se encontraron órdenes para el usuario '{}'", usuario);
            throw new ordenesNotFoundException("No se encontraron órdenes para el usuario: " + usuario);
        }
        return lista;
    }

    public List<ordenesModelo> findByEstado(String estado) {
        log.info("[ordenes] Buscando órdenes con estado '{}'", estado);
        List<ordenesModelo> lista = ordenesRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            log.warn("[ordenes] No se encontraron órdenes con estado '{}'", estado);
            throw new ordenesNotFoundException("No se encontraron órdenes con estado: " + estado);
        }
        return lista;
    }

    public ordenesModelo crear(ordenesRequestDTO dto, String token) {
        log.info("[ordenes] Validando producto_id {} en servicio catalogo", dto.getProductoId());
        catalogoClienteDTO producto;
        try {
            producto = catalogoWebClient.obtenerProducto(dto.getProductoId(), token);
            log.info("[ordenes] Producto '{}' validado correctamente", producto.getNombre());
        } catch (Exception e) {
            log.error("[ordenes] Producto con id {} no encontrado en servicio catalogo", dto.getProductoId());
            throw new RuntimeException("Producto con id " + dto.getProductoId() + " no encontrado en catálogo");
        }
        if (!producto.getDisponible()) {
            log.warn("[ordenes] Producto '{}' no está disponible", producto.getNombre());
            throw new RuntimeException("El producto '" + producto.getNombre() + "' no está disponible");
        }
        if (producto.getStock() < dto.getCantidad()) {
            log.warn("[ordenes] Stock insuficiente para producto '{}'. Disponible: {}, Solicitado: {}", producto.getNombre(), producto.getStock(), dto.getCantidad());
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        ordenesModelo nueva = ordenesModelo.builder()
                .usuario(dto.getUsuario())
                .producto(producto.getNombre())
                .cantidad(dto.getCantidad())
                .estado(dto.getEstado().toUpperCase())
                .fechaCreacion(LocalDateTime.now())
                .build();
        ordenesModelo guardada = ordenesRepositorio.save(nueva);
        log.info("[ordenes] Orden creada con id {} para usuario '{}'", guardada.getId(), guardada.getUsuario());
        return guardada;
    }

    public ordenesModelo actualizar(Long id, ordenesRequestDTO dto) {
        log.info("[ordenes] Actualizando orden con id {}", id);
        ordenesModelo existente = findById(id);
        existente.setUsuario(dto.getUsuario());
        existente.setProducto(String.valueOf(dto.getProductoId()));
        existente.setCantidad(dto.getCantidad());
        existente.setEstado(dto.getEstado().toUpperCase());
        ordenesModelo actualizada = ordenesRepositorio.save(existente);
        log.info("[ordenes] Orden con id {} actualizada correctamente", id);
        return actualizada;
    }

    public String eliminar(Long id) {
        log.info("[ordenes] Eliminando orden con id {}", id);
        ordenesModelo existente = findById(id);
        ordenesRepositorio.delete(existente);
        log.info("[ordenes] Orden con id {} eliminada correctamente", id);
        return "Orden con id " + id + " eliminada | Usuario: " + existente.getUsuario() + " | Producto: " + existente.getProducto() + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = ordenesRepositorio.count();
        log.warn("[ordenes] Eliminando todas las órdenes. Total: {}", total);
        ordenesRepositorio.deleteAll();
        log.info("[ordenes] Todas las órdenes eliminadas correctamente");
        return "Se eliminaron " + total + " órdenes correctamente";
    }
}