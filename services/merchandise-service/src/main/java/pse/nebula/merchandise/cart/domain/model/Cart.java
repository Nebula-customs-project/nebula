package pse.nebula.merchandise.cart.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String userId;
    private List<CartItem> items;

    public Cart() {}

    public Cart(String userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<CartItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }
    public void setItems(List<CartItem> items) { this.items = items; }

    public static CartBuilder builder() { return new CartBuilder(); }

    public static class CartBuilder {
        private String userId;
        private List<CartItem> items;

        public CartBuilder userId(String userId) { this.userId = userId; return this; }
        public CartBuilder items(List<CartItem> items) { this.items = items; return this; }
        public Cart build() {
            return new Cart(userId, items);
        }
    }
}
