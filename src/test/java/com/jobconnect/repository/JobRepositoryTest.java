package com.jobconnect.repository;

import com.jobconnect.model.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    void save_and_findActiveTrue() {

        Job j = new Job();
        j.setTitle("Test Job");
        j.setActive(true);
        j.setLocation("Remote");
        j.setPostedAt(LocalDateTime.now());

        jobRepository.save(j);

        List<Job> list = jobRepository.findByActiveTrue();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getTitle()).isEqualTo("Test Job");
    }
}
