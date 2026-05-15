package Gl1tch_st0re.inventario.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class inventarioRequestDTO {
    @NotBlank(message = "El usuario no puede estar vacío")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;
}