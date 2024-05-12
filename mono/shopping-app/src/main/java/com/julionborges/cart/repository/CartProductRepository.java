package com.julionborges.cart.repository;

import com.julionborges.cart.model.CartProduct;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartProductRepository implements PanacheRepository<CartProduct> {

    public CartProduct findByCartIdAndProductId(Long cartId, Long productId) {
        return (CartProduct) Panache
                .getEntityManager()
                .createNativeQuery("""
                                select
                                	cvp.*
                                from
                                	cart_v_product cvp
                                where
                                	cvp.cart_id = :cartId
                                	and cvp.product_id = :productId
                                """, CartProduct.class)
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .getSingleResult();
    }

}
