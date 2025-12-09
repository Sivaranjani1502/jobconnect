package com.jobconnect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JobConnectE2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads_andHomePageAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void seekerDashboard_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/seeker/dashboard"))
                .andExpect(status().is3xxRedirection()); // redirected to login
    }
}
