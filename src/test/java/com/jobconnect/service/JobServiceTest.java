package com.jobconnect.service;

import com.jobconnect.model.Job;
import com.jobconnect.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock JobRepository jobRepository;
    @InjectMocks JobService jobService;

    @BeforeEach void init(){ MockitoAnnotations.openMocks(this); }

    @Test
    void createJobFromForm_savesJob() {
        Job j = new Job();
        j.setTitle("A");
        when(jobRepository.save(any())).thenReturn(j);

        Job saved = jobService.createJobFromForm("A","loc","FT","1000-2000","desc","req","2026-01-01");
        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("A");
        verify(jobRepository, times(1)).save(any());
    }

    @Test
    void getJobById_returnsWhenPresent() {
        Job j = new Job(); j.setTitle("X");
        when(jobRepository.findById(1L)).thenReturn(Optional.of(j));
        Job out = jobService.getJobById(1L);
        assertThat(out).isEqualTo(j);
    }
}
