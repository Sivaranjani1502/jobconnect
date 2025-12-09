package com.jobconnect.repository;

import com.jobconnect.model.Role;
import com.jobconnect.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUserWhenExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("testrepo@example.com");
        user.setPassword("pwd");
        user.setRole(Role.JOB_SEEKER);
        user.setPhoneNumber("9999999999");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("testrepo@example.com");

        assertTrue(found.isPresent());
        assertEquals("Test User", found.get().getName());
    }

    @Test
    void findByEmail_returnsEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByEmail("nope@example.com");
        assertTrue(found.isEmpty());
    }
}
