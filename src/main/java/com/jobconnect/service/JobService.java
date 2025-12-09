package com.jobconnect.service;

import com.jobconnect.model.Job;
import com.jobconnect.model.User;
import com.jobconnect.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserService userService;

    public JobService(JobRepository jobRepository, UserService userService) {
        this.jobRepository = jobRepository;
        this.userService = userService;
    }

    // All jobs for a specific employer (used in EmployerController)
    public List<Job> getJobsForEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    // All active jobs (used in SeekerController)
    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    // Single job by id (used in SeekerController)
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    // *** MAIN METHOD: called when you press "Post Job" ***
    public Job createJobFromForm(String title,
                                 String location,
                                 String jobType,
                                 String salaryRange,
                                 String description,
                                 String requirements,
                                 String deadline) {

        // Find currently logged-in employer
        String email = userService.getCurrentUserEmail();
        User employer = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in employer not found"));

        Job job = new Job();
        job.setTitle(title);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalary(salaryRange);
        job.setDescription(description);
        job.setRequirements(requirements);

        job.setActive(true);
        job.setEmployer(employer);

        // Optional defaults
        if (deadline != null && !deadline.isBlank()) {
            job.setDeadline(LocalDate.parse(deadline));  // "yyyy-MM-dd" from <input type="date">
        }
        job.setPostedAt(LocalDateTime.now());

        // This actually writes to the DB
        return jobRepository.save(job);
    }
    public void deleteJobById(Long id) {
        jobRepository.deleteById(id);
    }

    public void toggleJobActive(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setActive(!job.isActive());
        jobRepository.save(job);
    }
    public void updateJobFromForm(Long id,
                                  String title,
                                  String location,
                                  String jobType,
                                  String salaryRange,
                                  String description,
                                  String requirements,
                                  String deadline) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        job.setTitle(title);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalary(salaryRange);
        job.setDescription(description);
        job.setRequirements(requirements);

        if (deadline != null && !deadline.isBlank()) {
            job.setDeadline(LocalDate.parse(deadline));
        } else {
            job.setDeadline(null);  // or keep old value, your choice
        }

        jobRepository.save(job);
    }


}
