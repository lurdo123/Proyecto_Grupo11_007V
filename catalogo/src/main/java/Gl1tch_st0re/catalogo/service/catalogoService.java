package Gl1tch_st0re.catalogo.service;

import Gl1tch_st0re.catalogo.dto.request.catalogoRequestDTO;
import Gl1tch_st0re.catalogo.exceptions.catalogoNotFoundException;
import Gl1tch_st0re.catalogo.model.catalogoModel;
import Gl1tch_st0re.catalogo.repository.catalogoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class catalogoService {

    @Autowired
    private catalogoRepository catalogoRepository;

    public List<catalogoModel> findAll() {
        log.info("[catalogo] Listando todos los productos");
        return catalogoRepository.findAll();
    }

    public catalogoModel obtenerPorId(Long id) {
        log.info("[catalogo] Buscando producto con id {}", id);
        return catalogoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[catalogo] Producto con id {} no encontrado", id);
                    return new catalogoNotFoundException("Producto con id " + id + " no encontrado");
                });
    }

    public catalogoModel crear(catalogoRequestDTO dto) {
        log.info("[catalogo] Creando producto '{}'", dto.getNombre());
        catalogoModel producto = catalogoModel.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .categoria(dto.getCategoria())
                .marca(dto.getMarca())
                .disponible(dto.getDisponible())
                .build();
        catalogoModel guardado = catalogoRepository.save(producto);
        log.info("[catalogo] Producto '{}' creado con id {}", guardado.getNombre(), guardado.getId());
        return guardado;
    }

    public catalogoModel actualizar(Long id, catalogoRequestDTO dto) {
        log.info("[catalogo] Actualizando producto con id {}", id);
        catalogoModel producto = catalogoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[catalogo] Producto con id {} no encontrado para actualizar", id);
                    return new catalogoNotFoundException("Producto con id " + id + " no encontrado");
                });
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setMarca(dto.getMarca());
        producto.setDisponible(dto.getDisponible());
        catalogoModel actualizado = catalogoRepository.save(producto);
        log.info("[catalogo] Producto con id {} actualizado correctamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        log.info("[catalogo] Eliminando producto con id {}", id);
        catalogoModel producto = catalogoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[catalogo] Producto con id {} no encontrado para eliminar", id);
                    return new catalogoNotFoundException("Producto con id " + id + " no encontrado");
                });
        catalogoRepository.delete(producto);
        log.info("[catalogo] Producto con id {} eliminado correctamente", id);
    }

    public void eliminarTodos() {
        long total = catalogoRepository.count();
        log.warn("[catalogo] Eliminando todos los productos. Total: {}", total);
        catalogoRepository.deleteAll();
        log.info("[catalogo] Todos los productos eliminados correctamente");
    }
}