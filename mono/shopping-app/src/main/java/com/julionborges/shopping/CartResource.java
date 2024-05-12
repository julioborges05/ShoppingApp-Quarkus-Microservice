package com.julionborges.shopping;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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

}
