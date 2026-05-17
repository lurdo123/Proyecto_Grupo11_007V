package Gl1tch_st0re.clientes.service;

import Gl1tch_st0re.clientes.dto.request.perfilClienteRequestDTO;
import Gl1tch_st0re.clientes.exceptions.clienteNotFoundException;
import Gl1tch_st0re.clientes.model.perfilClienteModel;
import Gl1tch_st0re.clientes.repository.perfilClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class perfilClienteService {

    @Autowired
    private perfilClienteRepository perfilClienteRepository;

    public List<perfilClienteModel> findAll() {
        return perfilClienteRepository.findAll();
    }

    public perfilClienteModel obtenerPorId(Long id) {
        return perfilClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Perfil de cliente con id " + id + " no encontrado"));
    }

    public perfilClienteModel crear(perfilClienteRequestDTO dto) {
        boolean existe = perfilClienteRepository.existsByUsuarioId(dto.getUsuarioId());
        if (existe) {
            throw new RuntimeException("Ya existe un perfil para el usuario_id " + dto.getUsuarioId());
        }
        perfilClienteModel perfil = perfilClienteModel.builder()
                .usuarioId(dto.getUsuarioId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .telefono(dto.getTelefono())
                .nivelFidelidad(dto.getNivelFidelidad() != null ? dto.getNivelFidelidad() : "Bronce")
                .totalCompradoHistorico(dto.getTotalCompradoHistorico())
                .build();
        return perfilClienteRepository.save(perfil);
    }

    public perfilClienteModel actualizar(Long id, perfilClienteRequestDTO dto) {
        perfilClienteModel perfil = perfilClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Perfil de cliente con id " + id + " no encontrado"));

        boolean existe = perfilClienteRepository.existsByUsuarioIdAndIdNot(dto.getUsuarioId(), id);
        if (existe) {
            throw new RuntimeException("Ya existe otro perfil con el usuario_id " + dto.getUsuarioId());
        }

        perfil.setUsuarioId(dto.getUsuarioId());
        perfil.setNombre(dto.getNombre());
        perfil.setApellido(dto.getApellido());
        perfil.setTelefono(dto.getTelefono());
        perfil.setNivelFidelidad(dto.getNivelFidelidad() != null ? dto.getNivelFidelidad() : "Bronce");
        perfil.setTotalCompradoHistorico(dto.getTotalCompradoHistorico());
        return perfilClienteRepository.save(perfil);
    }

    public String eliminar(Long id) {
        perfilClienteModel perfil = perfilClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Perfil de cliente con id " + id + " no encontrado"));
        perfilClienteRepository.delete(perfil);
        return "Perfil con id " + id + " eliminado | Usuario ID: " + perfil.getUsuarioId() + " | Nombre: " + perfil.getNombre() + " " + perfil.getApellido();
    }

    public String eliminarTodos() {
        long total = perfilClienteRepository.count();
        perfilClienteRepository.deleteAll();
        return "Se eliminaron " + total + " perfiles de clientes correctamente";
    }
}