package Gl1tch_st0re.ordenes.servicio;

import Gl1tch_st0re.ordenes.dto.request.ordenesRequestDTO;
import Gl1tch_st0re.ordenes.exceptions.ordenesNotFoundException;
import Gl1tch_st0re.ordenes.modelo.ordenesModelo;
import Gl1tch_st0re.ordenes.repositorio.ordenesRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ordenesServicio {

    @Autowired
    private ordenesRepositorio ordenesRepositorio;

    public List<ordenesModelo> findAll() {
        return ordenesRepositorio.findAll();
    }

    public ordenesModelo findById(Long id) {
        return ordenesRepositorio.findById(id)
                .orElseThrow(() -> new ordenesNotFoundException(
                        "Orden con id " + id + " no encontrada"));
    }

    public List<ordenesModelo> findByUsuario(String usuario) {
        List<ordenesModelo> lista = ordenesRepositorio.findByUsuario(usuario);
        if (lista.isEmpty()) {
            throw new ordenesNotFoundException(
                    "No se encontraron órdenes para el usuario: " + usuario);
        }
        return lista;
    }

    public List<ordenesModelo> findByEstado(String estado) {
        List<ordenesModelo> lista = ordenesRepositorio.findByEstado(estado.toUpperCase());
        if (lista.isEmpty()) {
            throw new ordenesNotFoundException(
                    "No se encontraron órdenes con estado: " + estado);
        }
        return lista;
    }

    public ordenesModelo crear(ordenesRequestDTO dto) {
        ordenesModelo nueva = ordenesModelo.builder()
                .usuario(dto.getUsuario())
                .producto(dto.getProducto())
                .cantidad(dto.getCantidad())
                .estado(dto.getEstado().toUpperCase())
                .fechaCreacion(LocalDateTime.now())
                .build();
        return ordenesRepositorio.save(nueva);
    }

    public ordenesModelo actualizar(Long id, ordenesRequestDTO dto) {
        ordenesModelo existente = findById(id);
        existente.setUsuario(dto.getUsuario());
        existente.setProducto(dto.getProducto());
        existente.setCantidad(dto.getCantidad());
        existente.setEstado(dto.getEstado().toUpperCase());
        return ordenesRepositorio.save(existente);
    }

    public String eliminar(Long id) {
        ordenesModelo existente = findById(id);
        ordenesRepositorio.delete(existente);
        return "Orden con id " + id + " eliminada | Usuario: " + existente.getUsuario()
                + " | Producto: " + existente.getProducto()
                + " | Estado: " + existente.getEstado();
    }

    public String eliminarTodos() {
        long total = ordenesRepositorio.count();
        ordenesRepositorio.deleteAll();
        return "Se eliminaron " + total + " órdenes correctamente";
    }
}