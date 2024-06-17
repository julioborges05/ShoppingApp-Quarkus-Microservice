package com.julionborges.service;

import com.julionborges.dto.CartDTO;
import com.julionborges.dto.CartProductDTO;
import com.julionborges.dto.ProductDTO;
import com.julionborges.dto.UserDTO;
import com.julionborges.model.Cart;
import com.julionborges.model.CartProduct;
import com.julionborges.model.CartStatusEnum;
import com.julionborges.repository.CartProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CartService {

    @Inject
    @RestClient
    ProductService productService;

    @Inject
    @RestClient
    UserService userService;

    @Inject
    CartProductRepository cartProductRepository;

    public List<CartDTO> listAll() {
        List<Cart> cartList = Cart.listAll();

        if(cartList.isEmpty())
            return new ArrayList<>();

        return cartList
                .stream()
                .map(cart -> {
                    List<CartProductDTO> cartProductList = cart.getCartProductList()
                            .stream()
                            .map(cartProduct -> new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()))
                            .toList();

                    return new CartDTO(cart.getId(), cartProductList, CartStatusEnum.valueOf(cart.getCartStatus()),
                            cart.getTotalPrice(), cart.getUser().id());
                })
                .collect(Collectors.toList());
    }

    public CartDTO findById(Long id) {
        Cart cart = Cart.<Cart>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        List<CartProductDTO> productDTOList = cart.getCartProductList()
                .stream()
                .map(cartProduct -> new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()))
                .toList();

        return new CartDTO(cart.getId(), productDTOList, CartStatusEnum.valueOf(cart.getCartStatus()),
                cart.getTotalPrice(), cart.getUser().id());
    }

    @Transactional
    public CartDTO newCart(CartDTO cartDTO) {
        UserDTO user = userService.findById(cartDTO.userId());

        float totalPrice = updateProductQuantityAfterGetTotalPrice(cartDTO.products());

        Cart cart = new Cart(null, totalPrice, CartStatusEnum.PENDING.name(), null, user.id());
        cart.persist();

        List<CartProductDTO> savedCartProduct = new ArrayList<>();
        for(CartProductDTO cartProductDTO : cartDTO.products()) {
            ProductDTO product = productService.findById(cartProductDTO.productId());

            CartProduct cartProduct = new CartProduct(null, cart, product, cartProductDTO.quantity());
            cartProduct.persist();
            savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()));
        }

        return new CartDTO(cart.getId(), savedCartProduct, CartStatusEnum.valueOf(cart.getCartStatus()),
                cart.getTotalPrice(), cart.getUser().id());
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
        Cart cart = Cart.<Cart>findByIdOptional(cartDTO.id())
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        UserDTO user = userService.findById(cartDTO.userId());

        deleteCartProductsThatAreRemovesWhenUpdateTheCart(cartDTO, cart);

        float totalPrice = (float) 0;
        List<CartProductDTO> savedCartProduct = new ArrayList<>();

        for(CartProductDTO cartProductDTO : cartDTO.products()) {
            ProductDTO product = productService.findById(cartProductDTO.productId());

            if(product.quantity() < cartProductDTO.quantity())
                throw new RuntimeException("Quantidade de itens indisponivel");

            totalPrice += cartProductDtoIsAdded(cartProductDTO, cart.getCartProductList())
                    ? addCartProductWhenUpdateTheCartAndGetPrice(cart, savedCartProduct, cartProductDTO, product)
                    : updateCartProductAndGetPrice(cart, savedCartProduct, cartProductDTO, product);
        }

        cart.setTotalPrice(totalPrice);
        cart.setUser(user);
        cart.persist();

        return new CartDTO(cart.getId(), savedCartProduct, CartStatusEnum.valueOf(cart.getCartStatus()), totalPrice,
                cart.getUser().id());
    }

    private float updateCartProductAndGetPrice(Cart cart, List<CartProductDTO> savedCartProduct, CartProductDTO cartProductDTO,
                                               ProductDTO product) {
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.id());

        if((product.quantity() + cartProduct.getProductQuantity()) < cartProductDTO.quantity())
            throw new RuntimeException("Quantidade de itens indisponivel");

        float price = cartProductDTO.quantity() * product.price();
        cartProduct.setProductQuantity(cartProductDTO.quantity());
        cartProduct.persist();
        savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()));
        return price;
    }

    private float addCartProductWhenUpdateTheCartAndGetPrice(Cart cart, List<CartProductDTO> savedCartProduct,
                                                             CartProductDTO cartProductDTO, ProductDTO product) {
        float price = cartProductDTO.quantity() * product.price();
        productService.updateProduct(new ProductDTO(product.id(), product.name(), product.price(),
                (product.quantity() - cartProductDTO.quantity())));

        CartProduct cartProduct = new CartProduct(null, cart, product, cartProductDTO.quantity());
        cartProduct.persist();
        savedCartProduct.add(new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()));
        return price;
    }

    private void deleteCartProductsThatAreRemovesWhenUpdateTheCart(CartDTO cartDTO, Cart cart) {
        for(CartProduct cartProduct : cart.getCartProductList()) {
            if(cartProductIsRemovedFromCartProductDTO(cartProduct, cartDTO.products()))
                CartProduct.deleteById(cartProduct.getId());
        }
    }

    private boolean cartProductDtoIsAdded(CartProductDTO cartProductDTO, List<CartProduct> cartProductList) {
        for(CartProduct cartProduct : cartProductList) {
            if(cartProduct.getProduct().id().equals(cartProductDTO.productId()))
                return false;
        }

        return true;
    }

    private boolean cartProductIsRemovedFromCartProductDTO(CartProduct cartProduct, List<CartProductDTO> cartProductDTOList) {
        for(CartProductDTO cartProductDTO : cartProductDTOList) {
            if(cartProduct.getProduct().id().equals(cartProductDTO.productId()))
                return false;
        }

        return true;
    }

    @Transactional
    public Long deleteById(Long id) {
        Cart cart = Cart.<Cart>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        for(CartProduct cartProduct : cart.getCartProductList()) {
            ProductDTO product = new ProductDTO(
                    cartProduct.getProduct().id(),
                    cartProduct.getProduct().name(),
                    cartProduct.getProduct().price(),
                    (cartProduct.getProduct().quantity() + cartProduct.getProductQuantity())
            );

            productService.updateProduct(product);
        }

        cart.delete();

        return cart.getId();
    }

    @Transactional
    public CartDTO finishCartById(Long id) {
        Cart cart = Cart.<Cart>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        if(cart.getCartStatus().equals(CartStatusEnum.ABORTED.name()))
            throw new RuntimeException("Não é possível finalizar um carrinho abortado, crie um novo carrinho");

        cart.setCartStatus(CartStatusEnum.FINISHED.name());
        cart.persist();

        List<CartProductDTO> cartProductDTOList = cart.getCartProductList()
                .stream()
                .map(cartProduct -> new CartProductDTO(cartProduct.getProduct().id(), cartProduct.getProductQuantity()))
                .toList();

        return new CartDTO(cart.getId(), cartProductDTOList, CartStatusEnum.FINISHED, cart.getTotalPrice(),
                cart.getUser().id());
    }

    @Transactional
    public CartDTO abortCartById(Long id) {
        Cart cart = Cart.<Cart>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        for(CartProduct cartProduct : cart.getCartProductList()) {
            ProductDTO product = new ProductDTO(
                    cartProduct.getProduct().id(),
                    cartProduct.getProduct().name(),
                    cartProduct.getProduct().price(),
                    (cartProduct.getProduct().quantity() + cartProduct.getProductQuantity())
            );
            productService.updateProduct(product);

            cartProduct.setProductQuantity(0);
            cartProduct.persist();
        }

        cart.setCartStatus(CartStatusEnum.ABORTED.name());
        cart.persist();

        return new CartDTO(cart.getId(), null, CartStatusEnum.ABORTED, null, cart.getUser().id());
    }
}
