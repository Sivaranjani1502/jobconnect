package com.jobconnect.service;

import com.jobconnect.model.Role;
import com.jobconnect.model.User;
import com.jobconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsNotificationService smsNotificationService; // ⭐ FIXED (add this)
    private final EmailNotificationService emailNotificationService;
    // ⭐ FIXED CONSTRUCTOR: include smsNotificationService
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SmsNotificationService smsNotificationService,
                       EmailNotificationService emailNotificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsNotificationService = smsNotificationService;
        this.emailNotificationService = emailNotificationService;
    }

    // ---------- REGISTER ----------
    @Transactional
    public User registerUser(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.JOB_SEEKER);
        }

        // Save user first
        User saved = userRepository.save(user);

        // 🔔 SMS after successful registration
        smsNotificationService.sendSms(
                saved.getPhoneNumber(),
                "Welcome to JobConnect, " + saved.getFullName() + "! Your account has been created."
        );

        return saved;
    }

    // ---------- LOOKUPS ----------
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ---------- CURRENT USER HELPERS ----------
    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return auth.getName();
    }

    public String getCurrentUserName() {
        String email = getCurrentUserEmail();
        if (email == null) return null;

        return userRepository.findByEmail(email)
                .map(User::getName)
                .orElse(null);
    }

    public Role getCurrentUserRole() {
        String email = getCurrentUserEmail();
        if (email == null) return null;

        return userRepository.findByEmail(email)
                .map(User::getRole)
                .orElse(null);
    }
}
