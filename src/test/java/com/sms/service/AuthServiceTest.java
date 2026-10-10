package com.sms.service;

import com.sms.dao.UserDAO;
import com.sms.exception.AuthenticationException;
import com.sms.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthService using JUnit 5.
 * 
 * // [TESTING] JUnit 5 tests for service layer
 */
class AuthServiceTest {

    private AuthService authService;
    private MockUserDAO mockUserDAO;

    @BeforeEach
    void setUp() {
        mockUserDAO = new MockUserDAO();
        authService = new AuthService(mockUserDAO);

        // Preload sample user
        User user = new User();
        user.setId(1);
        user.setUsername("testadmin");
        user.setPassword(BCrypt.hashpw("secret123", BCrypt.gensalt(10)));
        user.setFullName("Test Administrator");
        user.setEmail("admin@test.com");
        user.setRole("ADMIN");
        mockUserDAO.create(user);
    }

    @Test
    @DisplayName("Should successfully authenticate user with correct BCrypt credentials")
    void testAuthenticateSuccess() {
        User authenticated = authService.authenticate("testadmin", "secret123");
        assertNotNull(authenticated, "Authenticated user should not be null");
        assertEquals("testadmin", authenticated.getUsername());
        assertTrue(authenticated.isAdmin());
    }

    @Test
    @DisplayName("Should throw AuthenticationException when given incorrect password")
    void testAuthenticateInvalidPassword() {
        assertThrows(AuthenticationException.class, () -> {
            authService.authenticate("testadmin", "wrongpassword");
        });
    }

    @Test
    @DisplayName("Should throw AuthenticationException when user does not exist")
    void testAuthenticateUserNotFound() {
        assertThrows(AuthenticationException.class, () -> {
            authService.authenticate("unknown_user", "somepassword");
        });
    }

    @Test
    @DisplayName("Should throw AuthenticationException when inputs are blank or null")
    void testAuthenticateEmptyInputs() {
        assertThrows(AuthenticationException.class, () -> {
            authService.authenticate("", "");
        });
        assertThrows(AuthenticationException.class, () -> {
            authService.authenticate(null, "password");
        });
    }

    @Test
    @DisplayName("Should correctly generate and verify BCrypt hash")
    void testHashPassword() {
        String plain = "college2024";
        String hash = authService.hashPassword(plain);
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(BCrypt.checkpw(plain, hash));
        assertFalse(BCrypt.checkpw("wrongpassword", hash));
    }

    /**
     * In-memory mock DAO for deterministic testing without external database dependencies.
     */
    private static class MockUserDAO implements UserDAO {
        private final List<User> users = new ArrayList<>();

        @Override
        public boolean create(User user) {
            users.add(user);
            return true;
        }

        @Override
        public boolean update(User user) {
            return true;
        }

        @Override
        public boolean delete(int id) {
            return users.removeIf(u -> u.getId() == id);
        }

        @Override
        public User findById(int id) {
            return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        }

        @Override
        public User findByUsername(String username) {
            return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
        }

        @Override
        public User findByEmail(String email) {
            return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(users);
        }

        @Override
        public int count() {
            return users.size();
        }
    }
}
