package Gl1tch_st0re.catalogo.service;

import Gl1tch_st0re.catalogo.dto.request.catalogoRequestDTO;
import Gl1tch_st0re.catalogo.exceptions.catalogoNotFoundException;
import Gl1tch_st0re.catalogo.model.catalogoModel;
import Gl1tch_st0re.catalogo.repository.catalogoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class catalogoService {

    @Autowired
    private catalogoRepository catalogoRepository;

    public List<catalogoModel> findAll() {
        return catalogoRepository.findAll();
    }

    public catalogoModel obtenerPorId(Long id) {
        return catalogoRepository.findById(id)
                .orElseThrow(() -> new catalogoNotFoundException("Producto con id " + id + " no encontrado"));
    }

    public catalogoModel crear(catalogoRequestDTO dto) {
        catalogoModel producto = catalogoModel.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .categoria(dto.getCategoria())
                .marca(dto.getMarca())
                .disponible(dto.getDisponible())
                .build();
        return catalogoRepository.save(producto);
    }

    public catalogoModel actualizar(Long id, catalogoRequestDTO dto) {
        catalogoModel producto = catalogoRepository.findById(id)
                .orElseThrow(() -> new catalogoNotFoundException("Producto con id " + id + " no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setMarca(dto.getMarca());
        producto.setDisponible(dto.getDisponible());

        return catalogoRepository.save(producto);
    }

    public void eliminar(Long id) {
        catalogoModel producto = catalogoRepository.findById(id)
                .orElseThrow(() -> new catalogoNotFoundException("Producto con id " + id + " no encontrado"));
        catalogoRepository.delete(producto);
    }

    public void eliminarTodos() {
        catalogoRepository.deleteAll();
    }
}