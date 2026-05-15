package Gl1tch_st0re.autenticacion.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
@Data
@AllArgsConstructor
public class loginResponseDTO {
    private String token;
    private String tipo;
}
