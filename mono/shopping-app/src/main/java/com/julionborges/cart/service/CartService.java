package com.julionborges.cart.service;

import com.julionborges.cart.model.CartProduct;
import com.julionborges.cart.model.CartStatusEnum;
import com.julionborges.cart.dto.CartDTO;
import com.julionborges.cart.dto.CartProductDTO;
import com.julionborges.cart.model.Cart;
import com.julionborges.cart.repository.CartProductRepository;
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

    @Inject
    private CartProductRepository cartProductRepository;

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
        float totalPrice = updateProductQuantityAfterGetTotalPrice(cartDTO.products());

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

    private float updateProductQuantityAfterGetTotalPrice(List<CartProductDTO> cartProductDTOList) {
        float totalPrice = (float) 0;

        for(CartProductDTO cartProductDTO : cartProductDTOList) {
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

    @Transactional
    public CartDTO updateCart(CartDTO cartDTO) {
        Cart cart = (Cart) Cart.findByIdOptional(cartDTO.id())
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        deleteCartProductsThatAreRemovesWhenUpdateTheCart(cartDTO, cart);

        float totalPrice = (float) 0;
        List<CartProductDTO> savedCartProduct = new ArrayList<>();

        for(CartProductDTO cartProductDTO : cartDTO.products()) {
            Product product = getProduct(cartProductDTO.productId());

            if(product.getQuantity() < cartProductDTO.quantity())
                throw new RuntimeException("Quantidade de itens indisponivel");

            totalPrice += cartProductDtoIsAdded(cartProductDTO, cart.getCartProductList())
                    ? addCartProductWhenUpdateTheCartAndGetPrice(cart, savedCartProduct, cartProductDTO, product)
                    : updateCartProductAndGetPrice(cart, savedCartProduct, cartProductDTO, product);
        }

        cart.setTotalPrice(totalPrice);
        cart.persist();

        return new CartDTO(cart.getId(), savedCartProduct, CartStatusEnum.valueOf(cart.getCartStatus()), totalPrice);
    }

    private float updateCartProductAndGetPrice(Cart cart, List<CartProductDTO> savedCartProduct, CartProductDTO cartProductDTO,
                                               Product product) {
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if((product.getQuantity() + cartProduct.getProductQuantity()) < cartProductDTO.quantity())
            throw new RuntimeException("Quantidade de itens indisponivel");

        float price = cartProductDTO.quantity() * product.getPrice();
        cartProduct.setProductQuantity(cartProductDTO.quantity());
        cartProduct.persist();
        savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().getId(), cartProduct.getProductQuantity()));
        return price;
    }

    private float addCartProductWhenUpdateTheCartAndGetPrice(Cart cart, List<CartProductDTO> savedCartProduct,
                                                             CartProductDTO cartProductDTO, Product product) {
        float price = cartProductDTO.quantity() * product.getPrice();
        productService.updateProduct(new ProductDTO(product.getId(), product.getName(), product.getPrice(),
                (product.getQuantity() - cartProductDTO.quantity())));

        CartProduct cartProduct = new CartProduct(null, cart, product, cartProductDTO.quantity());
        cartProduct.persist();
        savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().getId(), cartProduct.getProductQuantity()));
        return price;
    }

    private Product getProduct(Long productId) {
        return (Product) Product.findByIdOptional(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    private void deleteCartProductsThatAreRemovesWhenUpdateTheCart(CartDTO cartDTO, Cart cart) {
        for(CartProduct cartProduct : cart.getCartProductList()) {
            if(cartProductIsRemovedFromCartProductDTO(cartProduct, cartDTO.products()))
                CartProduct.deleteById(cartProduct.getId());
        }
    }

    private boolean cartProductDtoIsAdded(CartProductDTO cartProductDTO, List<CartProduct> cartProductList) {
        for(CartProduct cartProduct : cartProductList) {
            if(cartProduct.getProduct().getId().equals(cartProductDTO.productId()))
                return false;
        }

        return true;
    }

    private boolean cartProductIsRemovedFromCartProductDTO(CartProduct cartProduct, List<CartProductDTO> cartProductDTOList) {
        for(CartProductDTO cartProductDTO : cartProductDTOList) {
            if(cartProduct.getProduct().getId().equals(cartProductDTO.productId()))
                return false;
        }

        return true;
    }

    @Transactional
    public Long deleteById(Long id) {
        Cart cart = (Cart) Cart.findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        for(CartProduct cartProduct : cart.getCartProductList()) {
            Product product = cartProduct.getProduct();
            product.setQuantity(product.getQuantity() + cartProduct.getProductQuantity());
            product.persist();
        }

        cart.delete();

        return cart.getId();
    }
}
