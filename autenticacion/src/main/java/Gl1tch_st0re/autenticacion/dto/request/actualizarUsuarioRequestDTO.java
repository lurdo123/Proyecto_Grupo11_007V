package Gl1tch_st0re.autenticacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class actualizarUsuarioRequestDTO {

    @NotBlank(message = "El usuario no puede estar vacío")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}