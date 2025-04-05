package com.moviescatalog.model;

import com.moviescatalog.dto.UserDto;
import com.moviescatalog.enums.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public static User fromDto(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }

}
