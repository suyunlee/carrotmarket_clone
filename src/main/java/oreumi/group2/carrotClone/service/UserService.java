package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.dto.UserDTO;
import oreumi.group2.carrotClone.model.User;

import java.util.Optional;

public interface UserService {

    User register(UserDTO userDTO);
    Optional<User> findByUsername(String username);
    User updateUser(User user);
}