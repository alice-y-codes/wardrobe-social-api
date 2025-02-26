package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.configs.TestSecurityConfig;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Use @MockBean to mock services in WebMvcTest

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("alice");
        user.setPassword("password");
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        // Arrange
        Mockito.when(userService.registerUser(any(User.class))).thenReturn(user);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))) // Convert user object to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));

        // Verify that service method was called once
        Mockito.verify(userService, Mockito.times(1)).registerUser(any(User.class));
    }
}
