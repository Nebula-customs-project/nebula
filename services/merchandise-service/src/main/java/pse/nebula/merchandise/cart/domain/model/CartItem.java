package pse.nebula.merchandise.cart.domain.model;

import java.math.BigDecimal;

public class CartItem {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    public CartItem() {}

    public CartItem(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public static CartItemBuilder builder() { return new CartItemBuilder(); }

    public static class CartItemBuilder {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;

        public CartItemBuilder productId(Long productId) { this.productId = productId; return this; }
        public CartItemBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public CartItemBuilder price(BigDecimal price) { this.price = price; return this; }
        public CartItem build() {
            return new CartItem(productId, quantity, price);
        }
    }
}
