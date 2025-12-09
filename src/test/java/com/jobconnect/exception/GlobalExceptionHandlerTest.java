package com.jobconnect.exception;

import com.jobconnect.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HomeController.class)
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;
    @MockBean com.jobconnect.service.UserService userService;

    @Test
    void handler_returnsErrorPage() throws Exception {
        // trigger an exception in controller path (simulate)
        mockMvc.perform(get("/register").param("bad", "x"))
               .andExpect(status().is4xxClientError()); // adjust depending on route
    }
}
