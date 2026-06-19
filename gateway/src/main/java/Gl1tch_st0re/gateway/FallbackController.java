package Gl1tch_st0re.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/autenticacion")
    public ResponseEntity<?> fallbackAutenticacion() {
        return respuesta("autenticacion", 8080);
    }

    @GetMapping("/catalogo")
    public ResponseEntity<?> fallbackCatalogo() {
        return respuesta("catalogo", 8081);
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> fallbackClientes() {
        return respuesta("clientes", 8082);
    }

    @GetMapping("/compatibilidad")
    public ResponseEntity<?> fallbackCompatibilidad() {
        return respuesta("compatibilidad", 8083);
    }

    @GetMapping("/envios")
    public ResponseEntity<?> fallbackEnvios() {
        return respuesta("envios", 8084);
    }

    @GetMapping("/garantias")
    public ResponseEntity<?> fallbackGarantias() {
        return respuesta("garantias", 8085);
    }

    @GetMapping("/inventario")
    public ResponseEntity<?> fallbackInventario() {
        return respuesta("inventario", 8086);
    }

    @GetMapping("/ordenes")
    public ResponseEntity<?> fallbackOrdenes() {
        return respuesta("ordenes", 8087);
    }

    @GetMapping("/pagos")
    public ResponseEntity<?> fallbackPagos() {
        return respuesta("pagos", 8088);
    }

    @GetMapping("/preventas")
    public ResponseEntity<?> fallbackPreventas() {
        return respuesta("preventas", 8089);
    }

    @GetMapping("/promociones")
    public ResponseEntity<?> fallbackPromociones() {
        return respuesta("promociones", 8090);
    }

    @GetMapping("/resenas")
    public ResponseEntity<?> fallbackResenas() {
        return respuesta("resenas", 8091);
    }

    private ResponseEntity<?> respuesta(String servicio, int puerto) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Servicio no disponible",
                        "servicio", servicio,
                        "puerto", puerto,
                        "mensaje", "El microservicio '" + servicio + "' no está respondiendo. Intente más tarde."
                ));
    }
}
