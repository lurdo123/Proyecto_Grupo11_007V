package Gl1tch_st0re.preventas.servicio;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.exceptions.preventasNotFoundException;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.repositorio.preventasRepositorio;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class preventasServicio {

    @Autowired
    private preventasRepositorio preventasRepositorio;

    public List<preventasModelo> findAll() {
        log.info("[preventas] Listando todas las preventas");
        return preventasRepositorio.findAll();
    }

    public preventasModelo findById(Long id) {
        log.info("[preventas] Buscando preventa con id {}", id);
        return preventasRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("[preventas] Preventa con id {} no encontrada", id);
                    return new preventasNotFoundException("Preventa con id " + id + " no encontrada");
                });
    }

    public List<preventasModelo> findByUsuario(String usuario) {
        log.info("[preventas] Buscando preventas para usuario '{}'", usuario);
        List<preventasModelo> lista = preventasRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            log.warn("[preventas] No se encontraron preventas para usuario '{}'", usuario);
            throw new preventasNotFoundException("No se encontraron preventas para el usuario: " + usuario);
        }
        return lista;
    }

    public List<preventasModelo> findByEstado(String estado) {
        log.info("[preventas] Buscando preventas con estado '{}'", estado);
        List<preventasModelo> lista = preventasRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            log.warn("[preventas] No se encontraron preventas con estado '{}'", estado);
            throw new preventasNotFoundException("No se encontraron preventas con estado: " + estado);
        }
        return lista;
    }

    public preventasModelo crear(preventasRequestDTO dto) {
        log.info("[preventas] Creando preventa para usuario '{}', producto '{}'", dto.getUsuario(), dto.getProducto());
        preventasModelo nueva = preventasModelo.builder()
                .usuario(dto.getUsuario())
                .producto(dto.getProducto())
                .cantidad(dto.getCantidad())
                .estado(dto.getEstado().toUpperCase())
                .fechaReserva(LocalDateTime.now())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .build();
        preventasModelo guardada = preventasRepositorio.save(nueva);
        log.info("[preventas] Preventa creada con id {}", guardada.getId());
        return guardada;
    }

    public preventasModelo actualizar(Long id, preventasRequestDTO dto) {
        log.info("[preventas] Actualizando preventa con id {}", id);
        preventasModelo existente = findById(id);
        existente.setUsuario(dto.getUsuario());
        existente.setProducto(dto.getProducto());
        existente.setCantidad(dto.getCantidad());
        existente.setEstado(dto.getEstado().toUpperCase());
        existente.setFechaLanzamiento(dto.getFechaLanzamiento());
        preventasModelo actualizada = preventasRepositorio.save(existente);
        log.info("[preventas] Preventa con id {} actualizada correctamente", id);
        return actualizada;
    }

    public String eliminar(Long id) {
        log.info("[preventas] Eliminando preventa con id {}", id);
        preventasModelo existente = findById(id);
        preventasRepositorio.delete(existente);
        log.info("[preventas] Preventa con id {} eliminada correctamente", id);
        return "Preventa con id " + id + " eliminada | Usuario: " + existente.getUsuario() + " | Producto: " + existente.getProducto() + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = preventasRepositorio.count();
        log.warn("[preventas] Eliminando todas las preventas. Total: {}", total);
        preventasRepositorio.deleteAll();
        log.info("[preventas] Todas las preventas eliminadas correctamente");
        return "Se eliminaron " + total + " preventas correctamente";
    }
}