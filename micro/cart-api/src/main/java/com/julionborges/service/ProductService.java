package com.julionborges.service;

import com.julionborges.dto.ProductDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8081/product")
@Produces(MediaType.APPLICATION_JSON)
public interface ProductService {

    @GET
    public List<ProductDTO> listAll();

    @GET
    @Path("findById")
    public ProductDTO findById(@QueryParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDTO newProduct(ProductDTO newProduct);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDTO updateProduct(ProductDTO updateProduct);

    @DELETE
    public Long deleteById(@QueryParam("id") Long id);

}
