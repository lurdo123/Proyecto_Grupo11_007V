package Gl1tch_st0re.compatibilidad.dto.response;


import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class loginResponse {
    private String token;
    private String tipo;
}