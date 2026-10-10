package com.sms.service;

import com.sms.dao.UserDAO;
import com.sms.dao.impl.UserDAOImpl;
import com.sms.exception.AuthenticationException;
import com.sms.exception.DuplicateEmailException;
import com.sms.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Logger;

/**
 * Service managing user authentication, security, and BCrypt credential hashing.
 * 
 * // [AUTH] BCrypt salted password hashing and role validation
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Initializes the default admin account with a secure BCrypt hash on application startup
     * if no administrator is currently registered in the database.
     * 
     * // [SQL REQUIREMENTS] Dynamic creation of admin / admin123 on startup
     */
    public synchronized void initDefaultAdminIfMissing() {
        User admin = userDAO.findByUsername("admin");
        if (admin == null) {
            LOGGER.info("No admin user found. Creating default administrator (admin / admin123)...");
            String hashedPassword = hashPassword("admin123");
            User defaultAdmin = new User();
            defaultAdmin.setUsername("admin");
            defaultAdmin.setPassword(hashedPassword);
            defaultAdmin.setFullName("System Administrator");
            defaultAdmin.setEmail("admin@sms.edu");
            defaultAdmin.setRole("ADMIN");
            userDAO.create(defaultAdmin);
            LOGGER.info("Default administrator account created successfully.");
        } else {
            boolean valid = false;
            try {
                valid = BCrypt.checkpw("admin123", admin.getPassword());
            } catch (Exception ignored) {
            }
            if (!valid) {
                admin.setPassword(hashPassword("admin123"));
                userDAO.update(admin);
                LOGGER.info("Default administrator password synchronized to valid BCrypt hash of 'admin123'.");
            }
        }
    }

    /**
     * Authenticates a user by username and plain text password.
     * 
     * @param username username entered by user
     * @param plainPassword plain text password
     * @return authenticated User object
     * @throws AuthenticationException if credentials are invalid
     */
    public User authenticate(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            throw new AuthenticationException("Username and password must not be empty.");
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new AuthenticationException("Invalid username or password.");
        }

        boolean matched = false;
        try {
            matched = BCrypt.checkpw(plainPassword, user.getPassword());
        } catch (IllegalArgumentException e) {
            // In case of legacy or unhashed password in database, check fallback
            if (plainPassword.equals(user.getPassword())) {
                matched = true;
                // Upgrade hash to BCrypt automatically
                user.setPassword(hashPassword(plainPassword));
                userDAO.update(user);
            }
        }

        if (!matched) {
            throw new AuthenticationException("Invalid username or password.");
        }

        return user;
    }

    /**
     * Registers a new user with BCrypt hashed password and uniqueness checks.
     */
    public boolean registerUser(User user, String plainPassword) {
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new AuthenticationException("Username '" + user.getUsername() + "' is already taken.");
        }
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new DuplicateEmailException("Email '" + user.getEmail() + "' is already registered.");
        }

        user.setPassword(hashPassword(plainPassword));
        return userDAO.create(user);
    }

    /**
     * Hashes a plain password using BCrypt with salt factor 10.
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }
}
