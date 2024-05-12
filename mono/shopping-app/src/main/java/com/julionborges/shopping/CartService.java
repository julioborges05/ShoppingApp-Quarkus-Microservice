package com.julionborges.shopping;

import com.julionborges.shopping.cart.Cart;
import com.julionborges.shopping.cart.CartStatusEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartService {

    public List<CartDTO> listAll() {
        List<Cart> cartList = Cart.listAll();

        if(cartList.isEmpty())
            return new ArrayList<>();

        return cartList
                .stream()
                .map(cart -> {
                    List<CartProductDTO> cartProductList = cart.getCartProductList()
                            .stream()
                            .map(cartProduct -> new CartProductDTO(cartProduct.getId(), cartProduct.getProductQuantity()))
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
                .map(cartProduct -> new CartProductDTO(cartProduct.getId(), cartProduct.getProductQuantity()))
                .toList();

        return new CartDTO(cart.getId(), productDTOList, CartStatusEnum.valueOf(cart.getCartStatus()), cart.getTotalPrice());
    }

}
