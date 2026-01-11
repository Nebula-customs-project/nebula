package pse.nebula.merchandise.cart.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.application.service.ProductService;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartItemResponse;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.domain.model.Cart;
import pse.nebula.merchandise.cart.domain.model.CartItem;
import pse.nebula.merchandise.cart.domain.repository.CartRepository;

/**
 * Service layer for shopping cart operations.
 * Manages cart items including adding, retrieving, and removing products from user carts.
 */
@Service
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    /**
     * Adds an item to a user's shopping cart.
     * If the item already exists in the cart, the quantity is incremented.
     * If the item is new, it's added to the cart.
     * Note: The price is always updated to the current product price when items are added or modified.
     *
     * @param userId The unique identifier of the user
     * @param request The request containing product ID and quantity to add
     * @return CartResponse with updated cart contents
     * @throws ResponseStatusException with NOT_FOUND status if product doesn't exist
     * @throws ResponseStatusException with BAD_REQUEST status if requested quantity exceeds available stock
     */
    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        ProductResponse product = productService.findById(request.getProductId());
        
        // Validate stock availability
        int requestedQuantity = request.getQuantity();
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        List<CartItem> items = cart.getItems();
        CartItem existing = items.stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst()
            .orElse(null);
        
        int totalQuantity = requestedQuantity + (existing != null ? existing.getQuantity() : 0);
        if (totalQuantity > product.getStock()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Requested quantity (" + totalQuantity + ") exceeds available stock (" + product.getStock() + ")");
        }
        
        BigDecimal price = product.getPrice();
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(price);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(price)
                    .build();
            newItem.setCart(cart);
            items.add(newItem);
        }
        cart.setItems(items);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    /**
     * Retrieves a user's shopping cart.
     * If the cart doesn't exist, an empty cart is returned.
     *
     * @param userId The unique identifier of the user
     * @return CartResponse containing the user's cart items
     */
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        return toResponse(cart);
    }

    /**
     * Removes an item from a user's shopping cart.
     * If the cart or item doesn't exist, this operation is idempotent and no error is raised.
     *
     * @param userId The unique identifier of the user
     * @param productId The ID of the product to remove from the cart
     * @return CartResponse with updated cart contents
     */
    @Transactional
    public CartResponse removeItem(String userId, Long productId) {
        Cart cart = cartRepository.findById(userId).orElseGet(() -> new Cart(userId, new ArrayList<>()));
        List<CartItem> items = cart.getItems();

        items.removeIf(item -> item.getProductId().equals(productId));

        cartRepository.save(cart);
        return toResponse(cart);
    }



    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            itemResponses.add(new CartItemResponse(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return new CartResponse(cart.getUserId(), itemResponses);
    }
}
