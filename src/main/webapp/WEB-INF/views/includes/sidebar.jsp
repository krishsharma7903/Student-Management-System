<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<aside class="app-sidebar" id="appSidebar">
    <!-- Brand / Logo -->
    <div class="sidebar__header">
        <a href="${pageContext.request.contextPath}/dashboard" class="sidebar__brand">
            <div class="sidebar__logo-icon">
                <i class="bi bi-mortarboard-fill"></i>
            </div>
            <span>EduCore<span style="color: var(--accent); font-weight: 800;">SMS</span></span>
        </a>
    </div>

    <!-- Navigation Menu -->
    <nav class="sidebar__nav">
        <div class="sidebar__section-label">Main Navigation</div>

        <a href="${pageContext.request.contextPath}/dashboard" 
           class="sidebar__link ${pageContext.request.requestURI.endsWith('/dashboard') || activeMenu == 'dashboard' ? 'active' : ''}">
            <i class="bi bi-grid-1x2-fill"></i>
            <span>Dashboard</span>
        </a>

        <a href="${pageContext.request.contextPath}/students" 
           class="sidebar__link ${pageContext.request.requestURI.contains('/students') || activeMenu == 'students' ? 'active' : ''}">
            <i class="bi bi-people-fill"></i>
            <span>Students</span>
        </a>

        <a href="${pageContext.request.contextPath}/courses" 
           class="sidebar__link ${pageContext.request.requestURI.contains('/courses') || activeMenu == 'courses' ? 'active' : ''}">
            <i class="bi bi-book-half"></i>
            <span>Courses</span>
        </a>

        <a href="${pageContext.request.contextPath}/enrollments" 
           class="sidebar__link ${pageContext.request.requestURI.contains('/enrollments') || activeMenu == 'enrollments' ? 'active' : ''}">
            <i class="bi bi-person-check-fill"></i>
            <span>Enrollments</span>
        </a>

        <a href="${pageContext.request.contextPath}/marks" 
           class="sidebar__link ${pageContext.request.requestURI.contains('/marks') || activeMenu == 'marks' ? 'active' : ''}">
            <i class="bi bi-award-fill"></i>
            <span>Marks & Grades</span>
        </a>

        <div class="sidebar__section-label">Analytics & Reports</div>

        <a href="${pageContext.request.contextPath}/reports" 
           class="sidebar__link ${pageContext.request.requestURI.contains('/reports') || activeMenu == 'reports' ? 'active' : ''}">
            <i class="bi bi-bar-chart-line-fill"></i>
            <span>Reports</span>
        </a>
    </nav>

    <!-- Sidebar Footer / User Info -->
    <div class="sidebar__footer">
        <div class="user-snippet">
            <div class="user-snippet__info">
                <div class="avatar">
                    <c:out value="${fn:substring(sessionScope.user.fullName != null ? sessionScope.user.fullName : 'U', 0, 1)}" />
                </div>
                <div>
                    <div class="user-snippet__name"><c:out value="${sessionScope.user.fullName}" /></div>
                    <div class="user-snippet__role"><c:out value="${sessionScope.user.role}" /></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout-icon" title="Logout">
                <i class="bi bi-box-arrow-right"></i>
            </a>
        </div>
    </div>
</aside>
