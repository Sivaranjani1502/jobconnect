package com.jobconnect.controller;

import com.jobconnect.model.Job;
import com.jobconnect.model.User;
import com.jobconnect.service.ApplicationService;
import com.jobconnect.service.JobService;
import com.jobconnect.service.UserService;
import org.springframework.security.test.context.support.WithMockUser;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeekerController.class)
class SeekerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JobService jobService;

    @MockBean
    private ApplicationService applicationService;

    @Test
    void seekerDashboard_loadsPage() throws Exception {
        mockMvc.perform(get("/seeker/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("seeker-dashboard"));
    }

    @Test
    @WithMockUser(username="user@test", roles={"JOB_SEEKER"})
    void myApplications_requiresAuth() throws Exception {
        mockMvc.perform(get("/seeker/my-applications"))
                .andExpect(status().isOk());
    }
}
