package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.helpers.UserDtoValidator;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserManagementControllerTest {

        @Mock
        private UserManagementService userManagementService;

        @Mock
        private AuthUtils authUtils;

        @Mock
        private UserDtoValidator userDtoValidator;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User testUser;

        @InjectMocks
        private UserManagementController userManagementController;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(userManagementController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
                objectMapper = new ObjectMapper();
                testUser = User.builder()
                                .id(1L)
                                .profile(Profile.builder().id(1L).build())
                                .build();
        }

        @Test
        void registerUser() throws Exception {
                UserRegistrationDto registrationDto = createTestRegistrationDto();
                UserResponseDto responseDto = createTestUserResponse();

                when(userManagementService.registerUser(any(UserRegistrationDto.class))).thenReturn(responseDto);
                doNothing().when(userDtoValidator).validate(any(), any(BindingResult.class));

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists());
        }

        @Test
        void registerUser_UsernameExists() throws Exception {
                UserRegistrationDto registrationDto = createTestRegistrationDto();
                when(userManagementService.registerUser(any(UserRegistrationDto.class)))
                                .thenThrow(new UsernameAlreadyExistsException("Username already exists"));
                doNothing().when(userDtoValidator).validate(any(), any(BindingResult.class));

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Username already exists"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void changePassword() throws Exception {
                ChangePasswordDto passwordDto = createTestPasswordDto();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(userManagementService.existsById(1L)).thenReturn(true);
                doNothing().when(userManagementService).changePassword(eq(1L), any(ChangePasswordDto.class));

                mockMvc.perform(put("/api/users/1/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(passwordDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void changePassword_Unauthorized() throws Exception {
                ChangePasswordDto passwordDto = createTestPasswordDto();
                User differentUser = User.builder().id(2L).build();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(differentUser);
                when(userManagementService.existsById(1L)).thenReturn(true);

                mockMvc.perform(put("/api/users/1/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(passwordDto)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void deleteUser() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(userManagementService.existsById(1L)).thenReturn(true);
                doNothing().when(userManagementService).deleteUser(1L);

                mockMvc.perform(delete("/api/users/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void deleteUser_Unauthorized() throws Exception {
                User differentUser = User.builder().id(2L).build();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(differentUser);
                when(userManagementService.existsById(1L)).thenReturn(true);

                mockMvc.perform(delete("/api/users/1"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void deleteUser_NotFound() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(userManagementService.existsById(999L)).thenReturn(false);

                mockMvc.perform(delete("/api/users/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("User not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        private UserRegistrationDto createTestRegistrationDto() {
                return new UserRegistrationDto("testuser", "test@example.com", "testpass", User.Provider.GOOGLE);
        }

        private ChangePasswordDto createTestPasswordDto() {
                return new ChangePasswordDto("oldpass", "newpass");
        }

        private UserResponseDto createTestUserResponse() {
                return UserResponseDto.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();
        }
}
