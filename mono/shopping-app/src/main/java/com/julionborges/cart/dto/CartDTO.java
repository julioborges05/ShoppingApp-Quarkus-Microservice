package com.julionborges.cart.dto;

import com.julionborges.cart.model.CartStatusEnum;

import java.util.List;

public record CartDTO(Long id, List<CartProductDTO> products, CartStatusEnum cartStatus, Float total, Long userId) {
}
