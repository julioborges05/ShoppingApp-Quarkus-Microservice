package com.julionborges.dto;


import com.julionborges.model.CartStatusEnum;

import java.util.List;

public record CartDTO(Long id, List<CartProductDTO> products, CartStatusEnum cartStatus, Float total, Long userId) {
}
