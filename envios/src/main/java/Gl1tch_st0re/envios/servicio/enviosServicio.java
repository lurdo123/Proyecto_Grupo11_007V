package Gl1tch_st0re.envios.servicio;

import Gl1tch_st0re.envios.dto.request.enviosRequestDTO;
import Gl1tch_st0re.envios.exceptions.enviosNotFoundException;
import Gl1tch_st0re.envios.modelo.enviosModelo;
import Gl1tch_st0re.envios.repositorio.enviosRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class enviosServicio {

    @Autowired
    private enviosRepositorio enviosRepositorio;

    public List<enviosModelo> findAll() {
        return enviosRepositorio.findAll();
    }

    public enviosModelo findById(Long id) {
        return enviosRepositorio.findById(id)
                .orElseThrow(() -> new enviosNotFoundException(
                        "Envío con id " + id + " no encontrado"));
    }

    public List<enviosModelo> findByUsuario(String usuario) {
        List<enviosModelo> lista = enviosRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            throw new enviosNotFoundException(
                    "No se encontraron envíos para el usuario: " + usuario);
        }
        return lista;
    }

    public List<enviosModelo> findByEstado(String estado) {
        List<enviosModelo> lista = enviosRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            throw new enviosNotFoundException(
                    "No se encontraron envíos con estado: " + estado);
        }
        return lista;
    }

    public List<enviosModelo> findByOrdenId(Long ordenId) {
        List<enviosModelo> lista = enviosRepositorio.findByOrdenId(ordenId);
        if (lista.isEmpty()) {
            throw new enviosNotFoundException(
                    "No se encontraron envíos para la orden con id: " + ordenId);
        }
        return lista;
    }

    public enviosModelo crear(enviosRequestDTO dto) {
        enviosModelo nuevo = enviosModelo.builder()
                .ordenId(dto.getOrdenId())
                .usuario(dto.getUsuario())
                .direccion(dto.getDireccion())
                .estado(dto.getEstado().toUpperCase())
                .transportista(dto.getTransportista())
                .fechaEnvio(LocalDateTime.now())
                .fechaEntregaEstimada(dto.getFechaEntregaEstimada())
                .build();
        return enviosRepositorio.save(nuevo);
    }

    public enviosModelo actualizar(Long id, enviosRequestDTO dto) {
        enviosModelo existente = findById(id);
        existente.setOrdenId(dto.getOrdenId());
        existente.setUsuario(dto.getUsuario());
        existente.setDireccion(dto.getDireccion());
        existente.setEstado(dto.getEstado().toUpperCase());
        existente.setTransportista(dto.getTransportista());
        existente.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());
        return enviosRepositorio.save(existente);
    }

    public String eliminar(Long id) {
        enviosModelo existente = findById(id);
        enviosRepositorio.delete(existente);
        return "Envío con id " + id + " eliminado | Usuario: " + existente.getUsuario()
                + " | Dirección: " + existente.getDireccion()
                + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = enviosRepositorio.count();
        enviosRepositorio.deleteAll();
        return "Se eliminaron " + total + " envíos correctamente";
    }
}