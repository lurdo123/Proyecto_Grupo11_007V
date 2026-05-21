package Gl1tch_st0re.pagos.service;

import Gl1tch_st0re.pagos.client.ordenesWebClient;
import Gl1tch_st0re.pagos.dto.request.pagoRequestDTO;
import Gl1tch_st0re.pagos.dto.response.ordenClienteDTO;
import Gl1tch_st0re.pagos.exceptions.pagoNotFoundException;
import Gl1tch_st0re.pagos.model.pagoModel;
import Gl1tch_st0re.pagos.repository.pagoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class pagoService {

    @Autowired
    private pagoRepository pagoRepository;

    @Autowired
    private ordenesWebClient ordenesWebClient;

    public List<pagoModel> findAll() {
        log.info("[pagos] Listando todos los pagos");
        return pagoRepository.findAll();
    }

    public pagoModel obtenerPorId(Long id) {
        log.info("[pagos] Buscando pago con id {}", id);
        return pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[pagos] Pago con id {} no encontrado", id);
                    return new pagoNotFoundException("Pago con id " + id + " no encontrado");
                });
    }

    public pagoModel crear(pagoRequestDTO dto, String token) {
        log.info("[pagos] Validando orden_id {} en servicio ordenes", dto.getOrdenId());
        try {
            ordenClienteDTO orden = ordenesWebClient.obtenerOrden(dto.getOrdenId(), token);
            if (orden == null) throw new RuntimeException();
            log.info("[pagos] Orden {} validada correctamente", dto.getOrdenId());
        } catch (Exception e) {
            log.error("[pagos] Orden con id {} no encontrada en servicio ordenes", dto.getOrdenId());
            throw new RuntimeException("Orden con id " + dto.getOrdenId() + " no encontrada");
        }
        boolean existe = pagoRepository.existsByIdTransaccionExterna(dto.getIdTransaccionExterna());
        if (existe) {
            log.warn("[pagos] Ya existe pago con transacción '{}'", dto.getIdTransaccionExterna());
            throw new RuntimeException("Ya existe un pago con la transaccion " + dto.getIdTransaccionExterna());
        }
        pagoModel pago = pagoModel.builder()
                .ordenId(dto.getOrdenId())
                .idTransaccionExterna(dto.getIdTransaccionExterna())
                .metodoPago(dto.getMetodoPago())
                .montoPagado(dto.getMontoPagado())
                .estadoPago(dto.getEstadoPago())
                .build();
        pagoModel guardado = pagoRepository.save(pago);
        log.info("[pagos] Pago creado con id {} para orden {}", guardado.getId(), guardado.getOrdenId());
        return guardado;
    }

    public pagoModel actualizar(Long id, pagoRequestDTO dto) {
        log.info("[pagos] Actualizando pago con id {}", id);
        pagoModel pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[pagos] Pago con id {} no encontrado para actualizar", id);
                    return new pagoNotFoundException("Pago con id " + id + " no encontrado");
                });
        boolean existe = pagoRepository.existsByIdTransaccionExternaAndIdNot(dto.getIdTransaccionExterna(), id);
        if (existe) {
            log.warn("[pagos] Ya existe otro pago con transacción '{}'", dto.getIdTransaccionExterna());
            throw new RuntimeException("Ya existe otro pago con la transaccion " + dto.getIdTransaccionExterna());
        }
        pago.setOrdenId(dto.getOrdenId());
        pago.setIdTransaccionExterna(dto.getIdTransaccionExterna());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setEstadoPago(dto.getEstadoPago());
        pagoModel actualizado = pagoRepository.save(pago);
        log.info("[pagos] Pago con id {} actualizado correctamente", id);
        return actualizado;
    }

    public String eliminar(Long id) {
        log.info("[pagos] Eliminando pago con id {}", id);
        pagoModel pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[pagos] Pago con id {} no encontrado para eliminar", id);
                    return new pagoNotFoundException("Pago con id " + id + " no encontrado");
                });
        pagoRepository.delete(pago);
        log.info("[pagos] Pago con id {} eliminado correctamente", id);
        return "Pago con id " + id + " eliminado | Orden ID: " + pago.getOrdenId() + " | Transaccion: " + pago.getIdTransaccionExterna() + " | Estado: " + pago.getEstadoPago();
    }

    public String eliminarTodos() {
        long total = pagoRepository.count();
        log.warn("[pagos] Eliminando todos los pagos. Total: {}", total);
        pagoRepository.deleteAll();
        log.info("[pagos] Todos los pagos eliminados correctamente");
        return "Se eliminaron " + total + " pagos correctamente";
    }
}