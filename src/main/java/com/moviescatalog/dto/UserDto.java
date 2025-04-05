package com.moviescatalog.dto;

import com.moviescatalog.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Schema(description = "Name of the user", example = "Thallita")
    private String name;

    @NotNull(message = "Username cannot be null")
    @Schema(description = "Username", example = "tgyzik")
    private String username;

    @NotNull(message = "Email cannot be null")
    @Schema(description = "Email of the user", example = "thagyzik@gmail.com")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Schema(description = "Password", example = "test123!")
    private String password;

    @Schema(hidden = true)
    private String role;

    public static UserDto fromUser(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole().name());
        return dto;
    }
}
