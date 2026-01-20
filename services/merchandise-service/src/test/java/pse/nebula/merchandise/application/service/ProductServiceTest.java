package pse.nebula.merchandise.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import pse.nebula.merchandise.application.dto.ProductRequest;
import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.domain.model.Product;
import pse.nebula.merchandise.domain.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_mapsEntitiesToResponses() {
        Product product = Product.builder()
                .id(1L)
                .name("Hat")
                .description("Blue hat")
                .price(BigDecimal.TEN)
                .stock(5)
                .imageUrl("img")
                .build();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.findAll();

        assertEquals(1, responses.size());
        assertEquals("Hat", responses.get(0).name());
        assertEquals(BigDecimal.TEN, responses.get(0).price());
    }

    @Test
    void findById_notFound_throwsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.findById(1L));
    }

    @Test
    void create_persistsProductAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Hat", "Blue hat", BigDecimal.valueOf(12.50), 3, "img", null, null,
                null, null);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product toSave = invocation.getArgument(0);
            toSave.setId(99L);
            return toSave;
        });

        ProductResponse response = productService.create(request);

        verify(productRepository).save(argThat(p -> p.getName().equals("Hat") && p.getStock() == 3));
        assertEquals(99L, response.id());
        assertEquals("Hat", response.name());
        assertEquals(BigDecimal.valueOf(12.50), response.price());
    }

    @Test
    void update_updatesExistingProductAndReturnsResponse() {
        Product existing = Product.builder()
                .id(5L)
                .name("Old Name")
                .description("Old desc")
                .price(BigDecimal.valueOf(10.00))
                .stock(2)
                .imageUrl("old-img")
                .category("Old")
                .badge("OldBadge")
                .rating(BigDecimal.valueOf(3.5))
                .reviews(10)
                .build();
        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductRequest request = new ProductRequest(
                "New Name",
                "New desc",
                BigDecimal.valueOf(20.00),
                5,
                "new-img",
                "Apparel",
                "Premium",
                BigDecimal.valueOf(4.8),
                42);

        ProductResponse response = productService.update(5L, request);

        verify(productRepository).save(argThat(p -> p.getId().equals(5L)
                && p.getName().equals("New Name")
                && p.getDescription().equals("New desc")
                && p.getPrice().equals(BigDecimal.valueOf(20.00))
                && p.getStock().equals(5)
                && p.getImageUrl().equals("new-img")
                && p.getCategory().equals("Apparel")
                && p.getBadge().equals("Premium")
                && p.getRating().equals(BigDecimal.valueOf(4.8))
                && p.getReviews().equals(42)));

        assertEquals(5L, response.id());
        assertEquals("New Name", response.name());
        assertEquals(BigDecimal.valueOf(20.00), response.price());
    }

    @Test
    void delete_existingProduct_deletes() {
        when(productRepository.existsById(7L)).thenReturn(true);

        productService.delete(7L);

        verify(productRepository).deleteById(7L);
    }

    @Test
    void delete_missingProduct_throwsNotFound() {
        when(productRepository.existsById(7L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> productService.delete(7L));
        verify(productRepository, never()).deleteById(7L);
    }
}
