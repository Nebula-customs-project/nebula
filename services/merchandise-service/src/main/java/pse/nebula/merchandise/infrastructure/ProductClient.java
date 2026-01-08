package pse.nebula.merchandise.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import pse.nebula.merchandise.cart.application.dto.ProductResponse;

@Component
public class ProductClient {

    private final WebClient.Builder webClientBuilder;

    public ProductClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public ProductResponse getProduct(Long productId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://merchandise-service/api/v1/merchandise/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }
}
