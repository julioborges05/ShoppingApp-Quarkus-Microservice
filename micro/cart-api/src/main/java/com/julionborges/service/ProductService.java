package com.julionborges.service;

import com.julionborges.dto.ProductDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8081/product")
@Produces(MediaType.APPLICATION_JSON)
public interface ProductService {

    @GET
    public List<ProductDTO> listAll();

    @GET
    @Path("findById")
    @Timeout(2000)
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 5000,
            successThreshold = 2
    )
    @Fallback(fallbackMethod = "fallbackFindById")
    public ProductDTO findById(@QueryParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDTO newProduct(ProductDTO newProduct);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(5000)
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 5000,
            successThreshold = 2
    )
    @Fallback(fallbackMethod = "fallbackUpdateProduct")
    public ProductDTO updateProduct(ProductDTO updateProduct);

    @DELETE
    public Long deleteById(@QueryParam("id") Long id);

    default ProductDTO fallbackFindById(Long id) {
        throw new RuntimeException("Product service is unavailable");
    }

    default ProductDTO fallbackUpdateProduct(ProductDTO updateProduct) {
        throw new RuntimeException("Product service is unavailable");
    }

}
