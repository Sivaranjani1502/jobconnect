package com.jobconnect.controller;

import com.jobconnect.model.Job;
import com.jobconnect.model.User;
import com.jobconnect.service.ApplicationService;
import com.jobconnect.service.JobService;
import com.jobconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployerController.class)
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JobService jobService;
    @MockBean
    private ApplicationService applicationService;

    @Test
    @WithMockUser(username = "emp@example.com", roles = {"EMPLOYER"})
    void dashboard_showsJobsAndStats() throws Exception {
        User employer = new User();
        employer.setEmail("emp@example.com");
        employer.setName("Employer Name");

        Job job1 = new Job();
        job1.setId(1L);
        job1.setTitle("Job 1");
        job1.setActive(true);

        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Job 2");
        job2.setActive(false);

        when(userService.getCurrentUserEmail()).thenReturn("emp@example.com");
        when(userService.findByEmail("emp@example.com")).thenReturn(Optional.of(employer));
        when(jobService.getJobsForEmployer(employer)).thenReturn(List.of(job1, job2));
        when(applicationService.countApplicationsForJobs(List.of(job1, job2))).thenReturn(3L);

        mockMvc.perform(get("/employer/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer-dashboard"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attributeExists("totalJobsCount"))
                .andExpect(model().attributeExists("activeJobsCount"))
                .andExpect(model().attributeExists("totalApplicantsCount"));
    }
}
