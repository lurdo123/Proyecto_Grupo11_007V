package Gl1tch_st0re.clientes.service;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.exceptions.clienteNotFoundException;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.repository.clienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class clienteService {

    @Autowired
    private clienteRepository clienteRepository;

    public List<clienteModel> findAll() {
        return clienteRepository.findAll();
    }

    public clienteModel obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Cliente con id " + id + " no encontrado"));
    }

    public clienteModel crear(clienteRequestDTO dto) {
        boolean existe = clienteRepository.existsByUsuarioId(dto.getUsuarioId());
        if (existe) {
            throw new RuntimeException("Ya existe un perfil para el usuario_id " + dto.getUsuarioId());
        }
        clienteModel cliente = clienteModel.builder()
                .usuarioId(dto.getUsuarioId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .telefono(dto.getTelefono())
                .nivelFidelidad(dto.getNivelFidelidad() != null ? dto.getNivelFidelidad() : "Bronce")
                .totalCompradoHistorico(dto.getTotalCompradoHistorico())
                .build();
        return clienteRepository.save(cliente);
    }

    public clienteModel actualizar(Long id, clienteRequestDTO dto) {
        clienteModel cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Cliente con id " + id + " no encontrado"));

        boolean existe = clienteRepository.existsByUsuarioIdAndIdNot(dto.getUsuarioId(), id);
        if (existe) {
            throw new RuntimeException("Ya existe otro perfil con el usuario_id " + dto.getUsuarioId());
        }

        cliente.setUsuarioId(dto.getUsuarioId());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setNivelFidelidad(dto.getNivelFidelidad() != null ? dto.getNivelFidelidad() : "Bronce");
        cliente.setTotalCompradoHistorico(dto.getTotalCompradoHistorico());
        return clienteRepository.save(cliente);
    }

    public String eliminar(Long id) {
        clienteModel cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Cliente con id " + id + " no encontrado"));
        clienteRepository.delete(cliente);
        return "Cliente con id " + id + " eliminado | Usuario ID: " + cliente.getUsuarioId() + " | Nombre: " + cliente.getNombre() + " " + cliente.getApellido();
    }

    public String eliminarTodos() {
        long total = clienteRepository.count();
        clienteRepository.deleteAll();
        return "Se eliminaron " + total + " clientes correctamente";
    }
}