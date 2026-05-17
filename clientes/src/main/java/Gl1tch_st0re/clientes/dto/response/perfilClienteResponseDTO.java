package Gl1tch_st0re.clientes.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class perfilClienteResponseDTO {
    private Long id;
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String nivelFidelidad;
    private Double totalCompradoHistorico;
}