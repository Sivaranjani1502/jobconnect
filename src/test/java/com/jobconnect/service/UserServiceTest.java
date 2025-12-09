package com.jobconnect.service;

import com.jobconnect.model.Role;
import com.jobconnect.model.User;
import com.jobconnect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SmsNotificationService smsNotificationService;
    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_success_whenEmailNotExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plain");
        user.setName("Ranjani");
        user.setRole(null);
        user.setPhoneNumber("9360476874");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(user);

        assertNotNull(saved);
        assertEquals("ENCODED", saved.getPassword());
        assertEquals(Role.JOB_SEEKER, saved.getRole()); // default when null

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(smsNotificationService).sendSms(eq("9360476874"), contains("Welcome to JobConnect"));
        verify(emailNotificationService).sendEmail(eq("test@example.com"), contains("Welcome"), anyString());
    }

    @Test
    void registerUser_throwsWhenEmailAlreadyRegistered() {
        User user = new User();
        user.setEmail("dup@example.com");

        when(userRepository.findByEmail("dup@example.com"))
                .thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(smsNotificationService, emailNotificationService);
    }
}
