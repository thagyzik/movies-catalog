package com.moviescatalog.utils;

import com.moviescatalog.model.User;
import com.moviescatalog.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    private final UserService userService;

    public AuthenticationUtils(UserService userService) {
        this.userService = userService;
    }

    public User getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

}
