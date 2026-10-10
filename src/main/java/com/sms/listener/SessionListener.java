package com.sms.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * HTTP Session lifecycle listener tracking active concurrent user sessions
 * using thread-safe AtomicInteger.
 * 
 * // [CONCURRENCY] AtomicInteger for live 'active users' counter
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger LOGGER = Logger.getLogger(SessionListener.class.getName());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        AtomicInteger counter = (AtomicInteger) context.getAttribute("activeUsersCounter");
        if (counter != null) {
            int currentActive = counter.incrementAndGet();
            context.setAttribute("activeUsers", currentActive);
            LOGGER.info(() -> "HTTP Session created (ID: " + se.getSession().getId() + "). Active users count: " + currentActive);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        AtomicInteger counter = (AtomicInteger) context.getAttribute("activeUsersCounter");
        if (counter != null) {
            int currentActive = counter.decrementAndGet();
            if (currentActive < 0) {
                counter.set(0);
                currentActive = 0;
            }
            final int finalActive = currentActive;
            context.setAttribute("activeUsers", finalActive);
            LOGGER.info(() -> "HTTP Session destroyed (ID: " + se.getSession().getId() + "). Active users count: " + finalActive);
        }
    }
}
