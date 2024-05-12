package com.julionborges.cart.service;

import com.julionborges.cart.model.CartProduct;
import com.julionborges.cart.model.CartStatusEnum;
import com.julionborges.cart.dto.CartDTO;
import com.julionborges.cart.dto.CartProductDTO;
import com.julionborges.cart.model.Cart;
import com.julionborges.product.Product;
import com.julionborges.product.ProductDTO;
import com.julionborges.product.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartService {

    @Inject
    private ProductService productService;

    public List<CartDTO> listAll() {
        List<Cart> cartList = Cart.listAll();

        if(cartList.isEmpty())
            return new ArrayList<>();

        return cartList
                .stream()
                .map(cart -> {
                    List<CartProductDTO> cartProductList = cart.getCartProductList()
                            .stream()
                            .map(cartProduct -> new CartProductDTO(cartProduct.getProduct().getId(), cartProduct.getProductQuantity()))
                            .toList();

                    return new CartDTO(cart.getId(), cartProductList, CartStatusEnum.valueOf(cart.getCartStatus()),
                            cart.getTotalPrice());
                })
                .collect(Collectors.toList());
    }

    public CartDTO findById(Long id) {
        Optional<Cart> cartOptional = Cart.findByIdOptional(id);

        if(cartOptional.isEmpty())
            throw new NotFoundException("Carrinho não encontrado");

        Cart cart = cartOptional.get();

        List<CartProductDTO> productDTOList = cart.getCartProductList()
                .stream()
                .map(cartProduct -> new CartProductDTO(cartProduct.getProduct().getId(), cartProduct.getProductQuantity()))
                .toList();

        return new CartDTO(cart.getId(), productDTOList, CartStatusEnum.valueOf(cart.getCartStatus()), cart.getTotalPrice());
    }

    @Transactional
    public CartDTO newCart(CartDTO cartDTO) {
        float totalPrice = updateProductQuantityAfterGetTotalPrice(cartDTO);

        Cart cart = new Cart(null, totalPrice, CartStatusEnum.PENDING.name(), null);
        cart.persist();

        List<CartProductDTO> savedCartProduct = new ArrayList<>();
        for(CartProductDTO cartProductDTO : cartDTO.products()) {
            Product product = Product.findById(cartProductDTO.productId());

            CartProduct cartProduct = new CartProduct(null, cart, product, cartProductDTO.quantity());
            cartProduct.persist();
            savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().getId(), cartProduct.getProductQuantity()));
        }

        return new CartDTO(cart.getId(), savedCartProduct, CartStatusEnum.valueOf(cart.getCartStatus()), cart.getTotalPrice());
    }

    private float updateProductQuantityAfterGetTotalPrice(CartDTO cartDTO) {
        float totalPrice = (float) 0;

        for(CartProductDTO cartProductDTO : cartDTO.products()) {
            ProductDTO productDTO = productService.findById(cartProductDTO.productId());

            if(productDTO.quantity() < cartProductDTO.quantity())
                throw new RuntimeException("Quantidade de itens indisponivel");

            totalPrice += cartProductDTO.quantity() * productDTO.price();

            ProductDTO updatedProductDTO = new ProductDTO(productDTO.id(), productDTO.name(), productDTO.price(),
                    (productDTO.quantity() - cartProductDTO.quantity()));
            productService.updateProduct(updatedProductDTO);
        }
        return totalPrice;
    }
}
