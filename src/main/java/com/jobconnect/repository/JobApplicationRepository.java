package com.jobconnect.repository;

import com.jobconnect.model.Job;
import com.jobconnect.model.JobApplication;
import com.jobconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findBySeeker(User seeker);

    List<JobApplication> findByJobIn(List<Job> jobs);
    List<JobApplication> findByJob(Job job);

    long countByJobIn(List<Job> jobs);
}
