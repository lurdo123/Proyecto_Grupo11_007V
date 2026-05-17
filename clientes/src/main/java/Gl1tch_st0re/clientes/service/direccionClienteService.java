package Gl1tch_st0re.clientes.service;

import Gl1tch_st0re.clientes.dto.request.direccionClienteRequestDTO;
import Gl1tch_st0re.clientes.exceptions.clienteNotFoundException;
import Gl1tch_st0re.clientes.model.direccionClienteModel;
import Gl1tch_st0re.clientes.repository.direccionClienteRepository;
import Gl1tch_st0re.clientes.repository.perfilClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class direccionClienteService {

    @Autowired
    private direccionClienteRepository direccionClienteRepository;

    @Autowired
    private perfilClienteRepository perfilClienteRepository;

    public List<direccionClienteModel> findAll() {
        return direccionClienteRepository.findAll();
    }

    public List<direccionClienteModel> findByClienteId(Long clienteId) {
        return direccionClienteRepository.findByClienteId(clienteId);
    }

    public direccionClienteModel obtenerPorId(Long id) {
        return direccionClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Direccion con id " + id + " no encontrada"));
    }

    public direccionClienteModel crear(direccionClienteRequestDTO dto) {
        boolean clienteExiste = perfilClienteRepository.existsById(dto.getClienteId());
        if (!clienteExiste) {
            throw new clienteNotFoundException("No existe un perfil de cliente con id " + dto.getClienteId());
        }
        direccionClienteModel direccion = direccionClienteModel.builder()
                .clienteId(dto.getClienteId())
                .tipoDireccion(dto.getTipoDireccion())
                .calleNumero(dto.getCalleNumero())
                .comunaCiudad(dto.getComunaCiudad())
                .regionEstado(dto.getRegionEstado())
                .esPrincipal(dto.getEsPrincipal())
                .build();
        return direccionClienteRepository.save(direccion);
    }

    public direccionClienteModel actualizar(Long id, direccionClienteRequestDTO dto) {
        direccionClienteModel direccion = direccionClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Direccion con id " + id + " no encontrada"));

        boolean clienteExiste = perfilClienteRepository.existsById(dto.getClienteId());
        if (!clienteExiste) {
            throw new clienteNotFoundException("No existe un perfil de cliente con id " + dto.getClienteId());
        }

        direccion.setClienteId(dto.getClienteId());
        direccion.setTipoDireccion(dto.getTipoDireccion());
        direccion.setCalleNumero(dto.getCalleNumero());
        direccion.setComunaCiudad(dto.getComunaCiudad());
        direccion.setRegionEstado(dto.getRegionEstado());
        direccion.setEsPrincipal(dto.getEsPrincipal());
        return direccionClienteRepository.save(direccion);
    }

    public String eliminar(Long id) {
        direccionClienteModel direccion = direccionClienteRepository.findById(id)
                .orElseThrow(() -> new clienteNotFoundException("Direccion con id " + id + " no encontrada"));
        direccionClienteRepository.delete(direccion);
        return "Direccion con id " + id + " eliminada | Cliente ID: " + direccion.getClienteId() + " | Tipo: " + direccion.getTipoDireccion() + " | Calle: " + direccion.getCalleNumero();
    }

    public String eliminarTodos() {
        long total = direccionClienteRepository.count();
        direccionClienteRepository.deleteAll();
        return "Se eliminaron " + total + " direcciones correctamente";
    }
}