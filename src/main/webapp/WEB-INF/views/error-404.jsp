<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found | EduCore SMS</title>
    <!-- Fonts & Icons -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Poppins:wght@600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="min-height: 100vh; display: flex; align-items: center; justify-content: center; background: var(--bg); padding: 1.5rem;">
    <div class="card" style="max-width: 500px; text-align: center; padding: 3rem 2rem;">
        <div style="font-size: 5rem; font-weight: 800; font-family: 'Poppins', sans-serif; color: var(--primary); line-height: 1;">404</div>
        <div style="width: 50px; height: 4px; background: var(--accent); margin: 1.25rem auto; border-radius: var(--radius-full);"></div>
        <h2 style="font-size: 1.45rem; font-weight: 700; margin-bottom: 0.75rem;">Page Not Found</h2>
        <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 2rem;">
            The academic portal resource or URL you requested could not be located on the server.
        </p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="display: inline-flex;">
            <i class="bi bi-house-door-fill"></i>
            <span>Return to Dashboard</span>
        </a>
    </div>
</body>
</html>
