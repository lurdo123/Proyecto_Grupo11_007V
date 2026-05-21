package Gl1tch_st0re.envios.servicio;

import Gl1tch_st0re.envios.client.ordenesWebClient;
import Gl1tch_st0re.envios.dto.request.enviosRequestDTO;
import Gl1tch_st0re.envios.dto.response.ordenClienteDTO;
import Gl1tch_st0re.envios.exceptions.enviosNotFoundException;
import Gl1tch_st0re.envios.modelo.enviosModelo;
import Gl1tch_st0re.envios.repositorio.enviosRepositorio;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class enviosServicio {

    @Autowired
    private enviosRepositorio enviosRepositorio;

    @Autowired
    private ordenesWebClient ordenesWebClient;

    public List<enviosModelo> findAll() {
        log.info("[envios] Listando todos los envíos");
        return enviosRepositorio.findAll();
    }

    public enviosModelo findById(Long id) {
        log.info("[envios] Buscando envío con id {}", id);
        return enviosRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("[envios] Envío con id {} no encontrado", id);
                    return new enviosNotFoundException("Envío con id " + id + " no encontrado");
                });
    }

    public List<enviosModelo> findByUsuario(String usuario) {
        log.info("[envios] Buscando envíos para usuario '{}'", usuario);
        List<enviosModelo> lista = enviosRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            log.warn("[envios] No se encontraron envíos para el usuario '{}'", usuario);
            throw new enviosNotFoundException("No se encontraron envíos para el usuario: " + usuario);
        }
        return lista;
    }

    public List<enviosModelo> findByEstado(String estado) {
        log.info("[envios] Buscando envíos con estado '{}'", estado);
        List<enviosModelo> lista = enviosRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            log.warn("[envios] No se encontraron envíos con estado '{}'", estado);
            throw new enviosNotFoundException("No se encontraron envíos con estado: " + estado);
        }
        return lista;
    }

    public List<enviosModelo> findByOrdenId(Long ordenId) {
        log.info("[envios] Buscando envíos para orden_id {}", ordenId);
        List<enviosModelo> lista = enviosRepositorio.findByOrdenId(ordenId);
        if (lista.isEmpty()) {
            log.warn("[envios] No se encontraron envíos para orden_id {}", ordenId);
            throw new enviosNotFoundException("No se encontraron envíos para la orden con id: " + ordenId);
        }
        return lista;
    }

    public enviosModelo crear(enviosRequestDTO dto, String token) {
        log.info("[envios] Validando orden_id {} en servicio ordenes", dto.getOrdenId());
        try {
            ordenClienteDTO orden = ordenesWebClient.obtenerOrden(dto.getOrdenId(), token);
            if (orden == null) throw new RuntimeException();
            log.info("[envios] Orden {} validada correctamente", dto.getOrdenId());
        } catch (Exception e) {
            log.error("[envios] Orden con id {} no encontrada en servicio ordenes", dto.getOrdenId());
            throw new RuntimeException("Orden con id " + dto.getOrdenId() + " no encontrada");
        }
        enviosModelo nuevo = enviosModelo.builder()
                .ordenId(dto.getOrdenId())
                .usuario(dto.getUsuario())
                .direccion(dto.getDireccion())
                .estado(dto.getEstado().toUpperCase())
                .transportista(dto.getTransportista())
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(dto.getFechaEntregaEstimada())
                .build();
        enviosModelo guardado = enviosRepositorio.save(nuevo);
        log.info("[envios] Envío creado con id {} para orden {}", guardado.getId(), guardado.getOrdenId());
        return guardado;
    }

    public enviosModelo actualizar(Long id, enviosRequestDTO dto) {
        log.info("[envios] Actualizando envío con id {}", id);
        enviosModelo existente = findById(id);
        existente.setOrdenId(dto.getOrdenId());
        existente.setUsuario(dto.getUsuario());
        existente.setDireccion(dto.getDireccion());
        existente.setEstado(dto.getEstado().toUpperCase());
        existente.setTransportista(dto.getTransportista());
        existente.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());
        enviosModelo actualizado = enviosRepositorio.save(existente);
        log.info("[envios] Envío con id {} actualizado correctamente", id);
        return actualizado;
    }

    public String eliminar(Long id) {
        log.info("[envios] Eliminando envío con id {}", id);
        enviosModelo existente = findById(id);
        enviosRepositorio.delete(existente);
        log.info("[envios] Envío con id {} eliminado correctamente", id);
        return "Envío con id " + id + " eliminado | Usuario: " + existente.getUsuario() + " | Dirección: " + existente.getDireccion() + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = enviosRepositorio.count();
        log.warn("[envios] Eliminando todos los envíos. Total: {}", total);
        enviosRepositorio.deleteAll();
        log.info("[envios] Todos los envíos eliminados correctamente");
        return "Se eliminaron " + total + " envíos correctamente";
    }
}