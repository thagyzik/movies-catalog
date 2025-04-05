package com.moviescatalog.services;

import com.moviescatalog.config.SecurityConfig;
import com.moviescatalog.dto.UserDto;
import com.moviescatalog.enums.RoleEnum;
import com.moviescatalog.model.User;
import com.moviescatalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfig;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public User saveUser(UserDto userDto) {
        User user = User.fromDto(userDto);
        user.setRole(RoleEnum.USER);
        user.setPassword(securityConfig.passwordEncoder().encode(userDto.getPassword()));
        return userRepository.save(user);
    }

    public User updateRole(User user) {
        return userRepository.save(user);
    }
}
