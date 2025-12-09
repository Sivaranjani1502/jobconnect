package com.jobconnect.repository;

import com.jobconnect.model.Job;
import com.jobconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployer(User employer);
    List<Job> findByActiveTrue();
}
