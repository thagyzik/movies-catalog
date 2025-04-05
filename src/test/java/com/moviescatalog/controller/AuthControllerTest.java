package com.moviescatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescatalog.dto.AuthRequestDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

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
    void loginSuccess() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("user@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginFailure_WrongPassword() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("user@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void loginFailure_WrongEmail() throws Exception {
        AuthRequestDto request = new AuthRequestDto();
        request.setEmail("wronguser@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.error").value("Unauthorized access. Please log in to continue."));
    }
}