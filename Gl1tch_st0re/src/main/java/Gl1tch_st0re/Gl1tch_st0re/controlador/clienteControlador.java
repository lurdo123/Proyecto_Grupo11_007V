package Gl1tch_st0re.Gl1tch_st0re.controlador;

import Gl1tch_st0re.Gl1tch_st0re.modelo.clienteModelo;
import Gl1tch_st0re.Gl1tch_st0re.servicio.clienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")

public class clienteControlador {
    @Autowired
    private clienteServicio clienteServicio;

    @GetMapping
    public ResponseEntity<List<clienteModelo>> listar() {
        List<clienteModelo> clientes = clienteServicio.findAll();
        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clientes);
    }

}
