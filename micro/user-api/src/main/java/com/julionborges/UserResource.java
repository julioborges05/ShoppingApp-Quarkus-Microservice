package com.julionborges;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    public List<UserDTO> listAll() {
        return userService.listAll();
    }

    @GET
    @Path("findById")
    public UserDTO findById(@QueryParam("id") Long id) {
        return userService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDTO newUser(UserDTO userDTO) {
        return userService.newUser(userDTO);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDTO updateUser(UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @DELETE
    public Long deleteById(@QueryParam("id") Long id) {
        return userService.deleteById(id);
    }

}
