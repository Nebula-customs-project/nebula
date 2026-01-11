package pse.nebula.merchandise.application.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pse.nebula.merchandise.application.dto.ProductRequest;
import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.domain.model.Product;
import pse.nebula.merchandise.domain.repository.ProductRepository;

/**
 * Service layer for product management operations.
 * Provides functionality to create, retrieve, and manage products.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return List of all products as ProductResponse DTOs
     */
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Finds a product by its ID.
     *
     * @param id The unique identifier of the product
     * @return ProductResponse containing the product details
     * @throws ResponseStatusException with NOT_FOUND status if product doesn't exist
     */
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    /**
     * Creates a new product in the database.
     *
     * @param request The product details to create
     * @return ProductResponse containing the created product with generated ID
     */
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .stock(request.getStock())
            .imageUrl(request.getImageUrl())
            .build();

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl()
        );
    }
}
