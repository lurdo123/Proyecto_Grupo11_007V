package Gl1tch_st0re.resenas.service;

import Gl1tch_st0re.resenas.dto.request.resenaRequestDTO;
import Gl1tch_st0re.resenas.exceptions.resenaNotFoundException;
import Gl1tch_st0re.resenas.model.resenaModel;
import Gl1tch_st0re.resenas.repository.resenaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class resenaService {

    @Autowired
    private resenaRepository resenaRepository;

    public List<resenaModel> findAll() {
        return resenaRepository.findAll();
    }

    public resenaModel obtenerPorId(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new resenaNotFoundException("Resena con id " + id + " no encontrada"));
    }

    public resenaModel crear(resenaRequestDTO dto) {
        boolean existe = resenaRepository.existsByProductoIdAndUsuarioId(dto.getProductoId(), dto.getUsuarioId());
        if (existe) {
            throw new RuntimeException("Ya existe una resena del usuario " + dto.getUsuarioId() + " para el producto " + dto.getProductoId());
        }
        resenaModel resena = resenaModel.builder()
                .productoId(dto.getProductoId())
                .usuarioId(dto.getUsuarioId())
                .calificacion(dto.getCalificacion())
                .comentario(dto.getComentario())
                .esCompraVerificada(dto.getEsCompraVerificada())
                .fechaPublicacion(dto.getFechaPublicacion())
                .build();
        return resenaRepository.save(resena);
    }

    public resenaModel actualizar(Long id, resenaRequestDTO dto) {
        resenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> new resenaNotFoundException("Resena con id " + id + " no encontrada"));

        boolean existe = resenaRepository.existsByProductoIdAndUsuarioIdAndIdNot(dto.getProductoId(), dto.getUsuarioId(), id);
        if (existe) {
            throw new RuntimeException("Ya existe otra resena del usuario " + dto.getUsuarioId() + " para el producto " + dto.getProductoId());
        }

        resena.setProductoId(dto.getProductoId());
        resena.setUsuarioId(dto.getUsuarioId());
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setEsCompraVerificada(dto.getEsCompraVerificada());
        resena.setFechaPublicacion(dto.getFechaPublicacion());
        return resenaRepository.save(resena);
    }

    public String eliminar(Long id) {
        resenaModel resena = resenaRepository.findById(id)
                .orElseThrow(() -> new resenaNotFoundException("Resena con id " + id + " no encontrada"));
        resenaRepository.delete(resena);
        return "Resena con id " + id + " eliminada | Producto ID: " + resena.getProductoId() + " | Usuario ID: " + resena.getUsuarioId() + " | Calificacion: " + resena.getCalificacion();
    }

    public String eliminarTodos() {
        long total = resenaRepository.count();
        resenaRepository.deleteAll();
        return "Se eliminaron " + total + " resenas correctamente";
    }
}