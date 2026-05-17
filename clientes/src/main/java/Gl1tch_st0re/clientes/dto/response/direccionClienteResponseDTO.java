package Gl1tch_st0re.clientes.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class direccionClienteResponseDTO {
    private Long id;
    private Long clienteId;
    private String tipoDireccion;
    private String calleNumero;
    private String comunaCiudad;
    private String regionEstado;
    private Boolean esPrincipal;
}