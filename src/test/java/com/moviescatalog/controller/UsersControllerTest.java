package com.moviescatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescatalog.dto.UserDto;
import com.moviescatalog.enums.RoleEnum;
import com.moviescatalog.model.User;
import com.moviescatalog.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setUsername("user");
        user.setRole(RoleEnum.USER);

        UserDto userDto = UserDto.fromUser(user);
        userService.saveUser(userDto);
    }

    @Test
    void registerUserSuccess() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("newpassword");
        userDto.setUsername("newuser");

        mockMvc.perform(post("/users/registerUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.username").value("newuser"))
               .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void registerUser_InvalidDataProvided() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("newpassword");
        userDto.setUsername("newuser");

        mockMvc.perform(post("/users/registerUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.name").value("Name cannot be null"));
    }

    @Test
    void registerUserConflict() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Existing User");
        userDto.setEmail("user@example.com");
        userDto.setPassword("password");
        userDto.setUsername("user");

        mockMvc.perform(post("/users/registerUser")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRoleSuccess() throws Exception {
        mockMvc.perform(put("/users/updateRole/user/ADMIN"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void updateUserRoleUnauthorized() throws Exception {
        mockMvc.perform(put("/users/updateRole/user/ADMIN"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserRoleUserNotFound() throws Exception {
        mockMvc.perform(put("/users/updateRole/nonexistentuser/ADMIN"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("User not found"));
    }
}