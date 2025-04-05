package com.moviescatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescatalog.dto.RatingRequestDto;
import com.moviescatalog.dto.UserDto;
import com.moviescatalog.enums.RoleEnum;
import com.moviescatalog.model.User;
import com.moviescatalog.services.RatingService;
import com.moviescatalog.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        Mockito.reset(ratingService);

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setUsername("user");
        user.setRole(RoleEnum.USER);

        UserDto userDto = UserDto.fromUser(user);

        userService.saveUser(userDto);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void rateMovie_MovieNotFound() throws Exception {
        RatingRequestDto ratingDto = new RatingRequestDto();
        ratingDto.setMovieId(1L);
        ratingDto.setRate(4);

        mockMvc.perform(post("/movies/ratings/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ratingDto)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value("Movie not found for id: 1"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getMyRatingsSuccess() throws Exception {
        mockMvc.perform(get("/movies/ratings/byUser"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray());
    }
}