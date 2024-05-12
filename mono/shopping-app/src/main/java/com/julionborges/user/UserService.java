package com.julionborges.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
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
        User user = User.<User>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

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
        User user = User.<User>findByIdOptional(userDTO.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.setName(userDTO.name());
        user.persist();

        return new UserDTO(user.getId(), user.getName());
    }

    @Transactional
    public Long deleteById(Long id) {
        User user = User.<User>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.delete();

        return user.getId();
    }
}
