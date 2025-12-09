package com.jobconnect.controller;

import com.jobconnect.model.Role;
import com.jobconnect.model.User;
import com.jobconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false) // ignore Spring Security filters for these tests
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void home_returnsHomeView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void register_success_redirectsWithRegisteredParam() throws Exception {
        Mockito.when(userService.registerUser(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/register")
                        .param("role", "JOB_SEEKER")
                        .param("fullName", "Test User")
                        .param("phoneNumber", "9999999999")
                        .param("email", "user@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?registered=true"));
    }

    @Test
    void register_whenEmailDuplicate_returnsHomeWithErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Email already registered"))
                .when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/register")
                        .param("role", "JOB_SEEKER")
                        .param("fullName", "Test User")
                        .param("phoneNumber", "9999999999")
                        .param("email", "user@example.com")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
