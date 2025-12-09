package com.jobconnect.service;

import com.jobconnect.model.User;
import com.jobconnect.repository.UserRepository;
import com.jobconnect.security.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_returnsUserDetails() {

        User u = new User();
        u.setId(1L);
        u.setEmail("test@mail.com");
        u.setPassword("pass");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(u));

        UserDetails details = customUserDetailsService.loadUserByUsername("test@mail.com");

        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("test@mail.com");
    }
}
