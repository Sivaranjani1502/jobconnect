package com.jobconnect.service;

import com.jobconnect.model.ApplicationStatus;
import com.jobconnect.model.Job;
import com.jobconnect.model.JobApplication;
import com.jobconnect.model.User;
import com.jobconnect.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final SmsNotificationService smsNotificationService;
    private final EmailNotificationService emailNotificationService; 
    // ✅ SINGLE constructor – Spring will use this
    public ApplicationService(JobApplicationRepository jobApplicationRepository,
                              SmsNotificationService smsNotificationService,
                              EmailNotificationService emailNotificationService) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.smsNotificationService = smsNotificationService;
        this.emailNotificationService = emailNotificationService;
    }

    public List<JobApplication> getApplicationsForJob(Job job) {
        return jobApplicationRepository.findByJob(job);
    }

    public JobApplication getById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }

    @Transactional
    public void updateStatus(Long applicationId, ApplicationStatus status) {
        JobApplication app = getById(applicationId);
        app.setStatus(status);

        // 🔔 SMS to seeker on status change
        User seeker = app.getSeeker();
        if (seeker != null) {
            String msg = "Your application for '" + app.getJob().getTitle()
                    + "' is now " + status + " on JobConnect.";
            smsNotificationService.sendSms(seeker.getPhoneNumber(), msg);
         // ✉️ EMAIL
            emailNotificationService.sendEmail(
                    seeker.getEmail(),
                    "Application status updated - " + app.getJob().getTitle(),
                    "Hi " + seeker.getFullName() + ",\n\n" +
                    msg + "\n\n" +
                    "Regards,\nJobConnect Team"
            );
        }
        // @Transactional will flush changes
    }

    @Transactional
    public JobApplication applyToJob(Job job,
                                     User seeker,
                                     String coverLetter,
                                     String resumeSummary) {

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setCoverLetter(coverLetter);
        application.setResumeSummary(resumeSummary);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        JobApplication saved = jobApplicationRepository.save(application);

        // 🔔 Dummy SMS: notify seeker
        if (seeker != null) {
            smsNotificationService.sendSms(
                    seeker.getPhoneNumber(),
                    "Hi " + seeker.getFullName() +
                            ", your application for '" + job.getTitle() +
                            "' has been submitted on JobConnect."
            );
         // ✉️ EMAIL
            emailNotificationService.sendEmail(
                    seeker.getEmail(),
                    "Application submitted - " + job.getTitle(),
                    "Hi " + seeker.getFullName() + ",\n\n" +
                    "Your application for '" + job.getTitle() +
                    "' has been submitted on JobConnect.\n\n" +
                    "We will notify you when the employer updates the status.\n\n" +
                    "Regards,\nJobConnect Team"
            );
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsForUser(User seeker) {
        return jobApplicationRepository.findBySeeker(seeker);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsForJobs(List<Job> jobs) {
        return jobApplicationRepository.findByJobIn(jobs);
    }

    @Transactional(readOnly = true)
    public long countApplicationsForJobs(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return 0L;
        }
        return jobApplicationRepository.countByJobIn(jobs);
    }

    @Transactional
    public void withdrawApplication(Long applicationId, User seeker) {
        if (applicationId == null || seeker == null) {
            return;
        }

        jobApplicationRepository.findById(applicationId).ifPresent(app -> {
            if (app.getSeeker() != null &&
                seeker.getId().equals(app.getSeeker().getId())) {
                jobApplicationRepository.delete(app);
            }
        });
    }
}
