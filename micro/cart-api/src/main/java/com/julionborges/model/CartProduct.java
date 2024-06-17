package com.julionborges.model;

import com.julionborges.dto.ProductDTO;
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

    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_quantity")
    private int productQuantity;
    @Transient
    private ProductDTO product;

    public CartProduct() {
    }

    public CartProduct(Long id, Cart cart, ProductDTO product, int productQuantity) {
        this.id = id;
        this.cart = cart;
        this.productId = product.id();
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }
}
