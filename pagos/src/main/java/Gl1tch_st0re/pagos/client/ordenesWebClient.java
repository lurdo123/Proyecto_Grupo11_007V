package Gl1tch_st0re.pagos.client;

import Gl1tch_st0re.pagos.dto.response.ordenClienteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ordenesWebClient {

    private final WebClient webClient;

    public ordenesWebClient(@Value("${ordenes.service.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public ordenClienteDTO obtenerOrden(Long id, String token) {
        return webClient.get()
                .uri("/api/ordenes/{id}", id)
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(ordenClienteDTO.class)
                .block();
    }
}