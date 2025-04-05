package com.moviescatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescatalog.dto.MovieDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MoviesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("admin123!");
        adminUser.setUsername("admin");
        adminUser.setRole(RoleEnum.ADMIN);

        UserDto userDto = UserDto.fromUser(adminUser);

        userService.saveUser(userDto);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void addMovieSuccess() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setName("Test Movie");
        movieDto.setCategory("Action");
        movieDto.setSynopsis("Test Synopsis");
        movieDto.setReleaseYear(2023);

        MockMultipartFile movieFile = new MockMultipartFile("movie", "", "application/json", objectMapper.writeValueAsBytes(movieDto));
        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "application/octet-stream", "test image content".getBytes());

        mockMvc.perform(multipart("/movies/addMovie")
                                .file(movieFile)
                                .file(imageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name").value("Test Movie"))
               .andExpect(jsonPath("$.category").value("Action"))
               .andExpect(jsonPath("$.synopsis").value("Test Synopsis"))
               .andExpect(jsonPath("$.releaseYear").value(2023))
               .andExpect(jsonPath("$.createdBy").value("admin"))
               .andExpect(jsonPath("$.createdAt").value(any(String.class)))
               .andExpect(jsonPath("$.imageEncoded").value(any(String.class)))
               .andExpect(jsonPath("$.id").value(any(Integer.class)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void addMovieFailure_InvalidData() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setCategory("Action");
        movieDto.setSynopsis("Test Synopsis");
        movieDto.setReleaseYear(2023);

        MockMultipartFile movieFile = new MockMultipartFile("movie", "", "application/json", objectMapper.writeValueAsBytes(movieDto));

        mockMvc.perform(multipart("/movies/addMovie")
                                .file(movieFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void addMovieFailure_UnauthorizedUser() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setName("Test Movie");
        movieDto.setCategory("Action");
        movieDto.setSynopsis("Test Synopsis");
        movieDto.setReleaseYear(2023);

        MockMultipartFile movieFile = new MockMultipartFile("movie", "", "application/json", objectMapper.writeValueAsBytes(movieDto));
        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "application/octet-stream", "test image content".getBytes());

        mockMvc.perform(multipart("/movies/addMovie")
                                .file(movieFile)
                                .file(imageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isUnauthorized());
    }
}