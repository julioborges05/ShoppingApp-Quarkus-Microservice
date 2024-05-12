package com.julionborges.cart.model;

import com.julionborges.product.Product;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_v_product")
public class CartProduct extends PanacheEntityBase {

    @Id
    @SequenceGenerator(
            name = "cartProductSequence",
            sequenceName = "cart_v_product_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cartProductSequence")
    private Long id;
    @JoinColumn(name = "cart_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Cart cart;
    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;
    @Column(name = "product_quantity")
    private int productQuantity;

    public CartProduct() {
    }

    public CartProduct(Long id, Cart cart, Product product, int productQuantity) {
        this.id = id;
        this.cart = cart;
        this.product = product;
        this.productQuantity = productQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
