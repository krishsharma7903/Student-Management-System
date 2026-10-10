<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'EduCore SMS'}" /> | Student Management System</title>
    <!-- Google Fonts: Poppins (headings) + Inter (body) -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Poppins:wght@500;600;700;800&display=swap" rel="stylesheet">
    <!-- Bootstrap Icons via CDN -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Single SaaS Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-container">
    <jsp:include page="/WEB-INF/views/includes/sidebar.jsp" />

    <main class="app-main">
        <!-- Sticky Top Bar -->
        <header class="topbar">
            <div class="topbar__left">
                <button type="button" class="topbar__toggle-btn" id="sidebarToggleBtn" aria-label="Toggle Navigation">
                    <i class="bi bi-list"></i>
                </button>
                <h1 class="topbar__title"><c:out value="${pageTitle != null ? pageTitle : 'Dashboard'}" /></h1>
            </div>
            <div class="topbar__right">
                <!-- Live Active Users Counter via HttpSessionListener & AtomicInteger -->
                <div class="live-users-pill" title="Live concurrent users connected">
                    <span class="live-dot"></span>
                    <span><c:out value="${applicationScope.activeUsers != null ? applicationScope.activeUsers : 1}" /> Active</span>
                </div>

                <!-- Theme Toggle Button (Light/Dark Mode via localStorage) -->
                <button type="button" class="theme-toggle-btn" id="themeToggleBtn" title="Toggle Theme" aria-label="Toggle theme">
                    <i class="bi bi-moon-stars-fill"></i>
                </button>

                <div class="user-pill-header" style="display: flex; align-items: center; gap: 0.6rem; padding-left: 0.5rem; border-left: 1px solid var(--border);">
                    <div class="avatar" style="width: 36px; height: 36px; font-size: 0.85rem;">
                        <c:out value="${fn:substring(sessionScope.user.fullName != null ? sessionScope.user.fullName : 'A', 0, 1)}" />
                    </div>
                    <div style="display: flex; flex-direction: column;">
                        <span style="font-size: 0.85rem; font-weight: 600; line-height: 1.1;"><c:out value="${sessionScope.user.fullName}" /></span>
                        <span style="font-size: 0.72rem; color: var(--primary); font-weight: 600;"><c:out value="${sessionScope.user.role}" /></span>
                    </div>
                </div>
            </div>
        </header>

        <!-- Main Content Area -->
        <div class="content-wrapper">
            <!-- Flash Notification Messages -->
            <c:if test="${not empty sessionScope.flashSuccess}">
                <div class="alert alert--success">
                    <i class="bi bi-check-circle-fill" style="font-size: 1.25rem;"></i>
                    <span><c:out value="${sessionScope.flashSuccess}" /></span>
                </div>
                <c:remove var="flashSuccess" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert--danger">
                    <i class="bi bi-exclamation-triangle-fill" style="font-size: 1.25rem;"></i>
                    <span><c:out value="${sessionScope.flashError}" /></span>
                </div>
                <c:remove var="flashError" scope="session" />
            </c:if>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert--danger">
                    <i class="bi bi-x-circle-fill" style="font-size: 1.25rem;"></i>
                    <span><c:out value="${errorMessage}" /></span>
                </div>
            </c:if>
