package com.sms.filter;

import com.sms.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Security filter enforcing authentication and Role-Based Access Control (RBAC).
 * 
 * // [WEB] AuthFilter protecting every page except login & static assets
 * // [WEB] Role check for admin-only actions
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_EXTENSIONS = Set.of(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".svg", ".ico", ".woff", ".woff2", ".ttf"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        // 1. Allow public static assets and authentication endpoints
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Check for active authenticated user in session
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            // Unauthenticated user -> Redirect to login page
            res.sendRedirect(contextPath + "/login");
            return;
        }

        // 3. Enforce Role-Based Access Control (RBAC)
        if (!hasPermission(req, currentUser, path)) {
            session.setAttribute("flashError", "Access Denied: You do not possess Administrator privileges for this action.");
            res.sendRedirect(contextPath + "/dashboard");
            return;
        }

        // 4. Set anti-cache headers for secured academic views
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/") || path.equals("/login") || path.equals("/logout")) {
            return true;
        }
        for (String ext : PUBLIC_EXTENSIONS) {
            if (path.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enforces ADMIN role restrictions for destructive actions and course management.
     */
    private boolean hasPermission(HttpServletRequest req, User user, String path) {
        if (user.isAdmin()) {
            return true; // Administrators have unrestricted access
        }

        // Restrict Teacher from Course Creation/Editing/Deletion
        if (path.startsWith("/courses")) {
            String action = req.getParameter("action");
            if ("add".equalsIgnoreCase(action) || "edit".equalsIgnoreCase(action) || "delete".equalsIgnoreCase(action)) {
                return false;
            }
        }

        // Restrict Teacher from Deleting Students
        if (path.startsWith("/students")) {
            String action = req.getParameter("action");
            if ("delete".equalsIgnoreCase(action)) {
                return false;
            }
        }

        // Teachers can view students, enrollments, reports, and enter/edit marks
        return true;
    }
}
