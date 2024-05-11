package com.julionborges.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    public List<UserDTO> listAll() {
        return User.<User> listAll()
                .stream()
                .map(user -> new UserDTO(user.getId(), user.getName()))
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        Optional<User> userOptional = User.findByIdOptional(id);

        if(userOptional.isEmpty())
            throw new NotFoundException("Usuário não encontrado");

        User user = userOptional.get();

        return new UserDTO(user.getId(), user.getName());
    }

    @Transactional
    public UserDTO newUser(UserDTO userDTO) {
        User user = new User(userDTO.id(), userDTO.name());
        user.setId(null);
        user.persist();

        return new UserDTO(user.getId(), user.getName());
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        Optional<User> optionalUser = User.findByIdOptional(userDTO.id());

        if(optionalUser.isEmpty())
            throw new NotFoundException("Usuário não encontrado");

        User user = optionalUser.get();
        user.setName(userDTO.name());
        user.persist();

        return new UserDTO(user.getId(), user.getName());
    }

    @Transactional
    public Long deleteById(Long id) {
        Optional<User> optionalUser = User.findByIdOptional(id);

        if(optionalUser.isEmpty())
            throw new NotFoundException("Usuário não encontrado");

        User user = optionalUser.get();
        user.delete();

        return user.getId();
    }
}
