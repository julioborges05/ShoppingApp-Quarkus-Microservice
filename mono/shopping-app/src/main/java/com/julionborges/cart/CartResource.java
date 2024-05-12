package com.julionborges.cart;

import com.julionborges.cart.dto.CartDTO;
import com.julionborges.cart.service.CartService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("cart")
@Produces(MediaType.APPLICATION_JSON)
public class CartResource {

    @Inject
    private CartService cartService;

    @GET
    public List<CartDTO> listAll() {
        return cartService.listAll();
    }

    @GET
    @Path("findById")
    public CartDTO findById(@QueryParam("id") Long id) {
        return cartService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public CartDTO newCart(CartDTO cartDTO) {
        return cartService.newCart(cartDTO);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public CartDTO updateCart(CartDTO cartDTO) {
        return cartService.updateCart(cartDTO);
    }

    @DELETE
    public Long deleteCart(@QueryParam("id") Long id) {
        return cartService.deleteById(id);
    }

}
