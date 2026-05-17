package Gl1tch_st0re.preventas.servicio;

import Gl1tch_st0re.preventas.dto.request.preventasRequestDTO;
import Gl1tch_st0re.preventas.exceptions.preventasNotFoundException;
import Gl1tch_st0re.preventas.modelo.preventasModelo;
import Gl1tch_st0re.preventas.repositorio.preventasRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class preventasServicio {

    @Autowired
    private preventasRepositorio preventasRepositorio;

    public List<preventasModelo> findAll() {
        return preventasRepositorio.findAll();
    }

    public preventasModelo findById(Long id) {
        return preventasRepositorio.findById(id)
                .orElseThrow(() -> new preventasNotFoundException(
                        "Preventa con id " + id + " no encontrada"));
    }

    public List<preventasModelo> findByUsuario(String usuario) {
        List<preventasModelo> lista = preventasRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            throw new preventasNotFoundException(
                    "No se encontraron preventas para el usuario: " + usuario);
        }
        return lista;
    }

    public List<preventasModelo> findByEstado(String estado) {
        List<preventasModelo> lista = preventasRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            throw new preventasNotFoundException(
                    "No se encontraron preventas con estado: " + estado);
        }
        return lista;
    }

    public preventasModelo crear(preventasRequestDTO dto) {
        preventasModelo nueva = preventasModelo.builder()
                .usuario(dto.getUsuario())
                .producto(dto.getProducto())
                .cantidad(dto.getCantidad())
                .estado(dto.getEstado().toUpperCase())
                .fechaReserva(LocalDateTime.now())
                .fechaLanzamiento(dto.getFechaLanzamiento())
                .build();
        return preventasRepositorio.save(nueva);
    }

    public preventasModelo actualizar(Long id, preventasRequestDTO dto) {
        preventasModelo existente = findById(id);
        existente.setUsuario(dto.getUsuario());
        existente.setProducto(dto.getProducto());
        existente.setCantidad(dto.getCantidad());
        existente.setEstado(dto.getEstado().toUpperCase());
        existente.setFechaLanzamiento(dto.getFechaLanzamiento());
        return preventasRepositorio.save(existente);
    }

    public String eliminar(Long id) {
        preventasModelo existente = findById(id);
        preventasRepositorio.delete(existente);
        return "Preventa con id " + id + " eliminada | Usuario: " + existente.getUsuario()
                + " | Producto: " + existente.getProducto()
                + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = preventasRepositorio.count();
        preventasRepositorio.deleteAll();
        return "Se eliminaron " + total + " preventas correctamente";
    }
}