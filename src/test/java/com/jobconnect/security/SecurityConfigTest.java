package com.jobconnect.security;

import com.jobconnect.controller.EmployerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployerController.class)
class SecurityConfigTest {

    @Autowired MockMvc mockMvc;

    @MockBean
    com.jobconnect.service.UserService userService; // controller dependency
    @MockBean
    com.jobconnect.service.JobService jobService;
    @MockBean
    com.jobconnect.service.ApplicationService applicationService;

    @Test
    void employerEndpoint_forAnonymous_getsForbiddenOrRedirect() throws Exception {
        mockMvc.perform(get("/employer/dashboard"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "employer@test", roles = {"EMPLOYER"})
    void employerEndpoint_withEmployerRole_allows() throws Exception {
        mockMvc.perform(get("/employer/dashboard"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "seeker@test", roles = {"JOB_SEEKER"})
    void employerEndpoint_withSeekerRole_forbidden() throws Exception {
        mockMvc.perform(get("/employer/dashboard"))
               .andExpect(status().isForbidden());
    }
}
