package com.example.project.repository;

import com.example.project.entity.Role;
import com.example.project.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by username")
    void shouldSaveAndFindByUsername() {
        User user = new User("john_doe", "john@example.com", "pass123", "John Doe", Role.ROLE_CUSTOMER);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("john_doe");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfExistsByEmail() {
        User user = new User("alice", "alice@example.com", "pass123", "Alice Smith", Role.ROLE_CUSTOMER);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("alice@example.com");
        boolean notExists = userRepository.existsByEmail("other@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
