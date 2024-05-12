package com.julionborges.shopping;

import com.julionborges.shopping.cart.CartStatusEnum;

import java.util.List;

public record CartDTO(Long id, List<CartProductDTO> products, CartStatusEnum cartStatus, Float total) {
}
