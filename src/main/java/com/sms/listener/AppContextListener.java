package com.sms.listener;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.sms.service.AuthService;
import com.sms.util.ThreadPoolManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Application lifecycle listener.
 * Manages thread pool initialization, clean shutdown, and default admin provisioning.
 * 
 * // [CONCURRENCY] ServletContextListener manages thread pool startup & clean shutdown
 * // [SQL REQUIREMENTS] Dynamic initialization of default admin user
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        LOGGER.info("Student Management System is starting up...");

        // Initialize active users counter
        context.setAttribute("activeUsersCounter", new AtomicInteger(0));
        context.setAttribute("appName", "EduCore SMS");
        context.setAttribute("appVersion", "1.0.0");

        // 1. Initialize Thread Pool Manager
        ThreadPoolManager.getInstance();

        // 2. Provision default admin account if not present (admin / admin123)
        try {
            AuthService authService = new AuthService();
            authService.initDefaultAdminIfMissing();
        } catch (Exception e) {
            LOGGER.warning("Could not auto-provision default admin during startup: " + e.getMessage());
        }

        LOGGER.info("Student Management System initialization completed successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down Student Management System...");

        // 1. Cleanly terminate thread pool
        ThreadPoolManager.getInstance().shutdown();

        // 2. Cleanup MySQL driver threads to avoid memory leaks
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            LOGGER.info("MySQL AbandonedConnectionCleanupThread safely shut down.");
        } catch (Exception ignored) {
        }

        LOGGER.info("Student Management System shutdown complete.");
    }
}
