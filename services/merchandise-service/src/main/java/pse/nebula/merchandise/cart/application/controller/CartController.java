package pse.nebula.merchandise.cart.application.controller;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import pse.nebula.merchandise.cart.application.dto.AddCartItemRequest;
import pse.nebula.merchandise.cart.application.dto.CartResponse;
import pse.nebula.merchandise.cart.application.service.CartService;

@RestController
@RequestMapping("/api/v1/merchandise/carts")
public class CartController {
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private static String validateUserId(String userId) {
        if (userId == null || !USER_ID_PATTERN.matcher(userId).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
        return userId;
    }

    @PostMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItem(@PathVariable String userId, @Valid @RequestBody AddCartItemRequest request) {
        String sanitizedUserId = validateUserId(userId);
        return cartService.addItem(sanitizedUserId, request);
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable String userId) {
        String sanitizedUserId = validateUserId(userId);
        return cartService.getCart(sanitizedUserId);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable String userId, @PathVariable Long productId) {
        String sanitizedUserId = validateUserId(userId);
        cartService.removeItem(sanitizedUserId, productId);
    }
}
