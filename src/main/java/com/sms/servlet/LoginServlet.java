package com.sms.servlet;

import com.sms.exception.AuthenticationException;
import com.sms.model.User;
import com.sms.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Controller handling user authentication, session binding, and remember-me cookies.
 * 
 * // [WEB] Servlets: LoginServlet with Post-Redirect-Get pattern and Cookies
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String COOKIE_REMEMBER_USER = "sms_remembered_user";
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Already logged in -> redirect to dashboard
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        // Check for remembered username cookie
        String rememberedUsername = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_REMEMBER_USER.equals(c.getName())) {
                    rememberedUsername = c.getValue();
                    break;
                }
            }
        }
        req.setAttribute("rememberedUsername", rememberedUsername);

        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String rememberMe = req.getParameter("rememberMe");

        try {
            User user = authService.authenticate(username, password);

            // Authentication succeeded -> establish new session
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);

            // Handle Remember Username Cookie
            Cookie rememberCookie = new Cookie(COOKIE_REMEMBER_USER, username);
            if ("on".equalsIgnoreCase(rememberMe) || "true".equalsIgnoreCase(rememberMe)) {
                rememberCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            } else {
                rememberCookie.setMaxAge(0); // Remove cookie
            }
            rememberCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            resp.addCookie(rememberCookie);

            // Post-Redirect-Get pattern
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (AuthenticationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("rememberedUsername", username);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An unexpected error occurred during login. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
