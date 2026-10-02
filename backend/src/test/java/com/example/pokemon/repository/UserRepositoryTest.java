package com.example.pokemon.repository;

import com.example.pokemon.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User(
                "bao@example.com",
                "hashed-password");

        userRepository.save(user);

        var result = userRepository.findByEmailIgnoreCase(
                "BAO@EXAMPLE.COM");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail())
                .isEqualTo("bao@example.com");
    }
}
