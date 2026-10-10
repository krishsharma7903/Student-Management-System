<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | EduCore Student Management System</title>
    <!-- Fonts & Icons -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Poppins:wght@600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-body">

    <div class="login-card">
        <div class="login-brand">
            <div class="login-logo">
                <i class="bi bi-mortarboard-fill"></i>
            </div>
            <h1 class="login-title">EduCore SMS</h1>
            <p class="login-subtitle">College Student Management & Grading Portal</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert--danger">
                <i class="bi bi-exclamation-circle-fill"></i>
                <span><c:out value="${errorMessage}" /></span>
            </div>
        </c:if>

        <c:if test="${param.logout == 'true'}">
            <div class="alert alert--info">
                <i class="bi bi-info-circle-fill"></i>
                <span>You have been safely signed out.</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" novalidate>
            <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="usernameInput" class="form-label">Username</label>
                <div class="input-icon-group">
                    <i class="bi bi-person input-icon"></i>
                    <input type="text" id="usernameInput" name="username" class="form-control" 
                           placeholder="Enter your username" 
                           value="<c:out value='${not empty rememberedUsername ? rememberedUsername : param.username}' />" 
                           required autofocus autocomplete="username">
                </div>
            </div>

            <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="passwordInput" class="form-label">Password</label>
                <div class="input-icon-group">
                    <i class="bi bi-shield-lock input-icon"></i>
                    <input type="password" id="passwordInput" name="password" class="form-control" 
                           placeholder="Enter password" required autocomplete="current-password">
                    <button type="button" class="toggle-pw-btn" title="Toggle password visibility" aria-label="Toggle password">
                        <i class="bi bi-eye"></i>
                    </button>
                </div>
            </div>

            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem; font-size: 0.88rem;">
                <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer; color: var(--text);">
                    <input type="checkbox" name="rememberMe" value="true" ${not empty rememberedUsername ? 'checked' : ''}>
                    <span>Remember username</span>
                </label>
            </div>

            <button type="submit" class="btn btn-primary login-btn">
                <span>Sign In to Portal</span>
                <i class="bi bi-arrow-right"></i>
            </button>
        </form>

        <div style="margin-top: 1.75rem; padding-top: 1.25rem; border-top: 1px solid var(--border-light); font-size: 0.82rem; color: var(--text-muted); text-align: center;">
            <div style="font-weight: 600; margin-bottom: 0.35rem; color: var(--text);">Demo Credentials:</div>
            <div>Admin: <strong style="color: var(--primary);">admin</strong> / <strong>admin123</strong></div>
            <div>Teacher: <strong style="color: var(--accent);">prof_sharma</strong> / <strong>admin123</strong></div>
        </div>
    </div>

    <!-- Theme and interactions -->
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
