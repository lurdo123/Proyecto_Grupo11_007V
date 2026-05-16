package Gl1tch_st0re.inventario.service;

import Gl1tch_st0re.inventario.dto.request.inventarioRequestDTO;
import Gl1tch_st0re.inventario.exceptions.inventarioNotFoundException;
import Gl1tch_st0re.inventario.model.inventarioModel;
import Gl1tch_st0re.inventario.repository.inventarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class inventarioService {

    @Autowired
    private inventarioRepository inventarioRepository;

    public List<inventarioModel> findAll() {
        return inventarioRepository.findAll();
    }

    public inventarioModel obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new inventarioNotFoundException("Inventario con id " + id + " no encontrado"));
    }

 public inventarioModel crear(inventarioRequestDTO dto) {
    boolean existe = inventarioRepository.existsByProductoId(dto.getProductoId());
    if (existe) {
        throw new RuntimeException("Ya existe un inventario con el productoId " + dto.getProductoId());
    }
    inventarioModel inv = inventarioModel.builder()
            .productoId(dto.getProductoId())
            .estadoFisico(dto.getEstadoFisico())
            .cantidadDisponible(dto.getCantidadDisponible())
            .ubicacionBodega(dto.getUbicacionBodega())
            .build();
    return inventarioRepository.save(inv);
}

public inventarioModel actualizar(Long id, inventarioRequestDTO dto) {
    inventarioModel inv = inventarioRepository.findById(id)
            .orElseThrow(() -> new inventarioNotFoundException("Inventario con id " + id + " no encontrado"));

    boolean existe = inventarioRepository.existsByProductoIdAndIdNot(dto.getProductoId(), id);
    if (existe) {
        throw new RuntimeException("Ya existe otro inventario con el productoId " + dto.getProductoId());
    }

    inv.setProductoId(dto.getProductoId());
    inv.setEstadoFisico(dto.getEstadoFisico());
    inv.setCantidadDisponible(dto.getCantidadDisponible());
    inv.setUbicacionBodega(dto.getUbicacionBodega());
    return inventarioRepository.save(inv);
}

public String eliminar(Long id) {
    inventarioModel inv = inventarioRepository.findById(id)
            .orElseThrow(() -> new inventarioNotFoundException("Inventario con id " + id + " no encontrado"));
    inventarioRepository.delete(inv);
    return "Inventario con id " + id + " eliminado | Producto ID: " + inv.getProductoId() + " | Estado: " + inv.getEstadoFisico() + " | Bodega: " + inv.getUbicacionBodega();
}

public String eliminarTodos() {
    long total = inventarioRepository.count();
    inventarioRepository.deleteAll();
    return "Se eliminaron " + total + " registros de inventario correctamente";
}
}