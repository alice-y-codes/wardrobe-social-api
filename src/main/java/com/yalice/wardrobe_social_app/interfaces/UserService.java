package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByUsername(String username);
    User registerUser(User user);
}
