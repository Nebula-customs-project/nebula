package pse.nebula.merchandise.cart.application.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pse.nebula.merchandise.application.dto.ProductResponse;
import pse.nebula.merchandise.application.service.ProductService;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartItemResponse;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.domain.model.Cart;
import pse.nebula.merchandise.cart.domain.model.CartItem;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final RedisTemplate<String, Cart> redisTemplate;
    private final ProductService productService;
    private static final String KEY_PREFIX = "cart:";

    public CartService(RedisTemplate<String, Cart> redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        ProductResponse product = productService.findById(request.getProductId());

        Cart cart = loadCart(userId);
        List<CartItem> items = cart.getItems();

        CartItem existing = items.stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst()
            .orElse(null);

        BigDecimal price = product.getPrice();
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(price); // keep price in sync with merchandise-service
        } else {
            items.add(CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .price(price)
                    .build());
        }

        cart.setItems(items);
        saveCart(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(String userId) {
        Cart cart = loadCart(userId);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userId, Long productId) {
        Cart cart = loadCart(userId);
        List<CartItem> updated = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (!item.getProductId().equals(productId)) {
                updated.add(item);
            }
        }
        cart.setItems(updated);
        saveCart(cart);
        return toResponse(cart);
    }

    private Cart loadCart(String userId) {
        Cart cart = redisTemplate.opsForValue().get(key(userId));
        if (cart == null) {
            cart = Cart.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .build();
        }
        return cart;
    }

    private void saveCart(Cart cart) {
        redisTemplate.opsForValue().set(key(cart.getUserId()), cart);
    }

    private String key(String userId) {
        return KEY_PREFIX + userId;
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            itemResponses.add(new CartItemResponse(item.getProductId(), item.getQuantity(), item.getPrice()));
        }
        return new CartResponse(cart.getUserId(), itemResponses);
    }
}
