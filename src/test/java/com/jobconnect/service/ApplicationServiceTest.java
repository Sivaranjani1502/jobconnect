package com.jobconnect.service;

import com.jobconnect.model.*;
import com.jobconnect.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private SmsNotificationService smsNotificationService;
    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void applyToJob_savesApplication_andSendsSmsAndEmail() {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Full-Stack Developer");

        User seeker = new User();
        seeker.setId(10L);
        seeker.setName("Ranjani");
        seeker.setEmail("seeker@example.com");
        seeker.setPhoneNumber("9360476874");

        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> {
                    JobApplication app = invocation.getArgument(0);
                    app.setId(100L);
                    return app;
                });

        JobApplication result = applicationService.applyToJob(
                job,
                seeker,
                "I am interested",
                "https://resume.link"
        );

        assertNotNull(result.getId());
        assertEquals(job, result.getJob());
        assertEquals(seeker, result.getSeeker());
        assertEquals(ApplicationStatus.PENDING, result.getStatus());
        assertNotNull(result.getAppliedAt());

        verify(jobApplicationRepository).save(any(JobApplication.class));
        verify(smsNotificationService).sendSms(eq("9360476874"), contains("has been submitted"));
        verify(emailNotificationService).sendEmail(eq("seeker@example.com"),
                contains("Application submitted"),
                contains("has been submitted"));
    }

    @Test
    void updateStatus_updatesStatus_andSendsNotifications() {
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Full-Stack Developer");

        User seeker = new User();
        seeker.setId(10L);
        seeker.setName("Ranjani");
        seeker.setEmail("seeker@example.com");
        seeker.setPhoneNumber("9360476874");

        JobApplication app = new JobApplication();
        app.setId(100L);
        app.setJob(job);
        app.setSeeker(seeker);
        app.setStatus(ApplicationStatus.PENDING);

        when(jobApplicationRepository.findById(100L)).thenReturn(Optional.of(app));

        applicationService.updateStatus(100L, ApplicationStatus.ACCEPTED);

        assertEquals(ApplicationStatus.ACCEPTED, app.getStatus());
        verify(smsNotificationService).sendSms(eq("9360476874"), contains("is now ACCEPTED"));
        verify(emailNotificationService).sendEmail(eq("seeker@example.com"),
                contains("status updated"),
                contains("is now ACCEPTED"));
    }
}
