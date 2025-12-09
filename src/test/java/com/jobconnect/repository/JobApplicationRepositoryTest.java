package com.jobconnect.repository;

import com.jobconnect.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;

    @Test
    void findByJob_returnsApplicationsForThatJob() {
        User employer = new User();
        employer.setName("Employer");
        employer.setEmail("emp@ex.com");
        employer.setPassword("123");
        employer.setRole(Role.EMPLOYER);
        userRepository.save(employer);

        User seeker = new User();
        seeker.setName("Seeker");
        seeker.setEmail("seek@ex.com");
        seeker.setPassword("123");
        seeker.setRole(Role.JOB_SEEKER);
        userRepository.save(seeker);

        Job job = new Job();
        job.setTitle("Job 1");
        job.setEmployer(employer);
        job.setActive(true);
        jobRepository.save(job);

        JobApplication app = new JobApplication();
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus(ApplicationStatus.PENDING);
        app.setAppliedAt(LocalDateTime.now());
        jobApplicationRepository.save(app);

        List<JobApplication> apps = jobApplicationRepository.findByJob(job);

        assertEquals(1, apps.size());
        assertEquals(seeker.getEmail(), apps.get(0).getSeeker().getEmail());
    }
}
