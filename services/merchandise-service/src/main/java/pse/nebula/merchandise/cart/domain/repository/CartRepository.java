package pse.nebula.merchandise.cart.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pse.nebula.merchandise.cart.domain.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
}
