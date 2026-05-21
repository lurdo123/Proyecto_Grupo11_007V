package Gl1tch_st0re.inventario.client;

import Gl1tch_st0re.inventario.dto.response.catalogoClienteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class catalogoWebClient {

    private final WebClient webClient;

    public catalogoWebClient(@Value("${catalogo.service.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public catalogoClienteDTO obtenerProducto(Long id, String token) {
        return webClient.get()
                .uri("/api/catalogo/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(catalogoClienteDTO.class)
                .block();
    }
}