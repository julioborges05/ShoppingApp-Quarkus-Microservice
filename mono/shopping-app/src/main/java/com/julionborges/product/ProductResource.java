package com.julionborges.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("product")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductService productService;

    @GET
    public List<ProductDTO> listAll() {
        return productService.listAll();
    }

    @GET
    @Path("findById")
    public ProductDTO findById(@QueryParam("id") Long id) {
        return productService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDTO newProduct(ProductDTO newProduct) {
        return productService.newProduct(newProduct);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDTO updateProduct(ProductDTO updateProduct) {
        return productService.updateProduct(updateProduct);
    }

    @DELETE
    public Long deleteById(@QueryParam("id") Long id) {
        return productService.deleteById(id);
    }
}
