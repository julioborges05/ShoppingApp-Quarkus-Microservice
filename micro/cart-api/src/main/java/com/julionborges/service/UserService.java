package com.julionborges.service;

import com.julionborges.dto.UserDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8082/user")
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {

    @GET
    public List<UserDTO> listAll();

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
    public UserDTO findById(@QueryParam("id") Long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDTO newUser(UserDTO userDTO);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDTO updateUser(UserDTO userDTO);

    @DELETE
    public Long deleteById(@QueryParam("id") Long id);

    default UserDTO fallbackFindById(Long id) {
        throw new RuntimeException("User service is unavailable");
    }

}
