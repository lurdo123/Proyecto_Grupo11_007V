package Gl1tch_st0re.clientes.service;

import Gl1tch_st0re.clientes.dto.request.clienteRequestDTO;
import Gl1tch_st0re.clientes.exceptions.clienteNotFoundException;
import Gl1tch_st0re.clientes.model.clienteModel;
import Gl1tch_st0re.clientes.repository.clienteRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class clienteService {

    @Autowired
    private clienteRepository clienteRepository;

    public List<clienteModel> findAll() {
        log.info("[clientes] Listando todos los clientes");
        return clienteRepository.findAll();
    }

    public clienteModel obtenerPorId(Long id) {
        log.info("[clientes] Buscando cliente con id {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[clientes] Cliente con id {} no encontrado", id);
                    return new clienteNotFoundException("Cliente con id " + id + " no encontrado");
                });
    }

    public clienteModel crear(clienteRequestDTO dto) {
        log.info("[clientes] Creando cliente para usuario_id {}", dto.getUsuarioId());
        boolean existe = clienteRepository.existsByUsuarioId(dto.getUsuarioId());
        if (existe) {
            log.warn("[clientes] Ya existe un perfil para usuario_id {}", dto.getUsuarioId());
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
        clienteModel guardado = clienteRepository.save(cliente);
        log.info("[clientes] Cliente creado con id {}", guardado.getId());
        return guardado;
    }

    public clienteModel actualizar(Long id, clienteRequestDTO dto) {
        log.info("[clientes] Actualizando cliente con id {}", id);
        clienteModel cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[clientes] Cliente con id {} no encontrado para actualizar", id);
                    return new clienteNotFoundException("Cliente con id " + id + " no encontrado");
                });
        boolean existe = clienteRepository.existsByUsuarioIdAndIdNot(dto.getUsuarioId(), id);
        if (existe) {
            log.warn("[clientes] Ya existe otro perfil con usuario_id {}", dto.getUsuarioId());
            throw new RuntimeException("Ya existe otro perfil con el usuario_id " + dto.getUsuarioId());
        }
        cliente.setUsuarioId(dto.getUsuarioId());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setNivelFidelidad(dto.getNivelFidelidad() != null ? dto.getNivelFidelidad() : "Bronce");
        cliente.setTotalCompradoHistorico(dto.getTotalCompradoHistorico());
        clienteModel actualizado = clienteRepository.save(cliente);
        log.info("[clientes] Cliente con id {} actualizado correctamente", id);
        return actualizado;
    }

    public String eliminar(Long id) {
        log.info("[clientes] Eliminando cliente con id {}", id);
        clienteModel cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[clientes] Cliente con id {} no encontrado para eliminar", id);
                    return new clienteNotFoundException("Cliente con id " + id + " no encontrado");
                });
        clienteRepository.delete(cliente);
        log.info("[clientes] Cliente con id {} eliminado correctamente", id);
        return "Cliente con id " + id + " eliminado | Usuario ID: " + cliente.getUsuarioId() + " | Nombre: " + cliente.getNombre() + " " + cliente.getApellido();
    }

    public String eliminarTodos() {
        long total = clienteRepository.count();
        log.warn("[clientes] Eliminando todos los clientes. Total: {}", total);
        clienteRepository.deleteAll();
        log.info("[clientes] Todos los clientes eliminados correctamente");
        return "Se eliminaron " + total + " clientes correctamente";
    }
}