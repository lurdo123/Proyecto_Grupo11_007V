package Gl1tch_st0re.pagos.service;

import Gl1tch_st0re.pagos.dto.request.pagoRequestDTO;
import Gl1tch_st0re.pagos.exceptions.pagoNotFoundException;
import Gl1tch_st0re.pagos.model.pagoModel;
import Gl1tch_st0re.pagos.repository.pagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class pagoService {

    @Autowired
    private pagoRepository pagoRepository;

    public List<pagoModel> findAll() {
        return pagoRepository.findAll();
    }

    public pagoModel obtenerPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new pagoNotFoundException("Pago con id " + id + " no encontrado"));
    }

    public pagoModel crear(pagoRequestDTO dto) {
        boolean existe = pagoRepository.existsByIdTransaccionExterna(dto.getIdTransaccionExterna());
        if (existe) {
            throw new RuntimeException("Ya existe un pago con la transaccion " + dto.getIdTransaccionExterna());
        }
        pagoModel pago = pagoModel.builder()
                .ordenId(dto.getOrdenId())
                .idTransaccionExterna(dto.getIdTransaccionExterna())
                .metodoPago(dto.getMetodoPago())
                .montoPagado(dto.getMontoPagado())
                .estadoPago(dto.getEstadoPago())
                .build();
        return pagoRepository.save(pago);
    }

    public pagoModel actualizar(Long id, pagoRequestDTO dto) {
        pagoModel pago = pagoRepository.findById(id)
                .orElseThrow(() -> new pagoNotFoundException("Pago con id " + id + " no encontrado"));

        boolean existe = pagoRepository.existsByIdTransaccionExternaAndIdNot(dto.getIdTransaccionExterna(), id);
        if (existe) {
            throw new RuntimeException("Ya existe otro pago con la transaccion " + dto.getIdTransaccionExterna());
        }

        pago.setOrdenId(dto.getOrdenId());
        pago.setIdTransaccionExterna(dto.getIdTransaccionExterna());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setEstadoPago(dto.getEstadoPago());
        return pagoRepository.save(pago);
    }

    public String eliminar(Long id) {
        pagoModel pago = pagoRepository.findById(id)
                .orElseThrow(() -> new pagoNotFoundException("Pago con id " + id + " no encontrado"));
        pagoRepository.delete(pago);
        return "Pago con id " + id + " eliminado | Orden ID: " + pago.getOrdenId() + " | Transaccion: " + pago.getIdTransaccionExterna() + " | Estado: " + pago.getEstadoPago();
    }

    public String eliminarTodos() {
        long total = pagoRepository.count();
        pagoRepository.deleteAll();
        return "Se eliminaron " + total + " pagos correctamente";
    }
}