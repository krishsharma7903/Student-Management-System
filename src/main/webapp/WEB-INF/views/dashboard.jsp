<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="System Dashboard" scope="request" />
<c:set var="activeMenu" value="dashboard" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<!-- 4 Gradient Stat Cards -->
<div class="stats-grid">
    <!-- Stat 1: Total Students -->
    <div class="stat-card stat-card--blue">
        <div class="stat-card__info">
            <span class="stat-card__label">Total Enrolled</span>
            <span class="stat-card__value"><c:out value="${totalStudents}" /></span>
            <span class="stat-card__badge" style="color: var(--primary);">
                <i class="bi bi-people-fill"></i> Active Students
            </span>
        </div>
        <div class="stat-card__icon-wrap">
            <i class="bi bi-mortarboard-fill"></i>
        </div>
    </div>

    <!-- Stat 2: Total Courses -->
    <div class="stat-card stat-card--teal">
        <div class="stat-card__info">
            <span class="stat-card__label">Course Catalog</span>
            <span class="stat-card__value"><c:out value="${totalCourses}" /></span>
            <span class="stat-card__badge" style="color: var(--accent);">
                <i class="bi bi-journal-code"></i> Accredited Subjects
            </span>
        </div>
        <div class="stat-card__icon-wrap">
            <i class="bi bi-book-half"></i>
        </div>
    </div>

    <!-- Stat 3: Total Enrollments -->
    <div class="stat-card stat-card--purple">
        <div class="stat-card__info">
            <span class="stat-card__label">Total Enrollments</span>
            <span class="stat-card__value"><c:out value="${totalEnrollments}" /></span>
            <span class="stat-card__badge" style="color: #9333EA;">
                <i class="bi bi-link-45deg"></i> Course Allocations
            </span>
        </div>
        <div class="stat-card__icon-wrap">
            <i class="bi bi-check2-circle"></i>
        </div>
    </div>

    <!-- Stat 4: Average Marks -->
    <div class="stat-card stat-card--amber">
        <div class="stat-card__info">
            <span class="stat-card__label">Institution Average</span>
            <span class="stat-card__value"><c:out value="${averageMarks}" />%</span>
            <span class="stat-card__badge" style="color: var(--warning);">
                <i class="bi bi-graph-up-arrow"></i> Aggregate Score
            </span>
        </div>
        <div class="stat-card__icon-wrap">
            <i class="bi bi-award-fill"></i>
        </div>
    </div>
</div>

<!-- Dashboard Grid (Recent Students + CSS-only Course Chart) -->
<div class="dashboard-grid">
    <!-- Left Column: Recent Students Table -->
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-clock-history" style="color: var(--primary);"></i>
                <span>Recently Registered Students</span>
            </h2>
            <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary btn-sm">
                <span>View All</span>
                <i class="bi bi-arrow-right"></i>
            </a>
        </div>

        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Roll Number</th>
                        <th>Student Name</th>
                        <th>Department</th>
                        <th>Semester</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="student" items="${recentStudents}">
                        <tr>
                            <td>
                                <strong style="color: var(--primary);"><c:out value="${student.rollNumber}" /></strong>
                            </td>
                            <td>
                                <div class="table-user-cell">
                                    <div class="avatar-sm">
                                        <c:out value="${fn:substring(student.name, 0, 1)}" />
                                    </div>
                                    <div>
                                        <div style="font-weight: 600;"><c:out value="${student.name}" /></div>
                                        <div style="font-size: 0.78rem; color: var(--text-muted);"><c:out value="${student.email}" /></div>
                                    </div>
                                </div>
                            </td>
                            <td><c:out value="${student.department}" /></td>
                            <td>Semester <c:out value="${student.semester}" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/students?action=view&id=${student.id}" 
                                   class="btn-icon-pill" title="View Profile">
                                    <i class="bi bi-eye"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty recentStudents}">
                        <tr>
                            <td colspan="5" class="empty-state">No students found.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Right Column: Course-Wise Enrollment CSS-Only Bar Chart -->
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-bar-chart-fill" style="color: var(--accent);"></i>
                <span>Course Enrollments</span>
            </h2>
            <span style="font-size: 0.8rem; color: var(--text-muted);">Realtime Distribution</span>
        </div>

        <div class="chart-container">
            <c:forEach var="entry" items="${courseEnrollments}">
                <c:set var="percent" value="${totalEnrollments > 0 ? (entry.value * 100) / totalEnrollments : 0}" />
                <div class="chart-bar-item">
                    <div class="chart-bar-label">
                        <span style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${entry.key}">
                            <c:out value="${entry.key}" />
                        </span>
                        <span><strong><c:out value="${entry.value}" /></strong> students</span>
                    </div>
                    <div class="chart-bar-track">
                        <div class="chart-bar-fill" style="width: ${percent}%;"></div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty courseEnrollments}">
                <div class="empty-state">No enrollment data available.</div>
            </c:if>
        </div>

        <!-- Academic Toppers Spotlight -->
        <div style="margin-top: 2rem; padding-top: 1.25rem; border-top: 1px solid var(--border-light);">
            <h3 style="font-size: 0.95rem; font-weight: 600; margin-bottom: 0.85rem; display: flex; align-items: center; gap: 0.5rem;">
                <i class="bi bi-trophy-fill" style="color: var(--warning);"></i>
                <span>Top Academic Performers</span>
            </h3>
            <div style="display: flex; flex-direction: column; gap: 0.65rem;">
                <c:forEach var="topper" items="${topStudents}">
                    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.5rem 0.75rem; background: var(--bg); border-radius: var(--radius-sm);">
                        <div style="display: flex; align-items: center; gap: 0.6rem;">
                            <div class="avatar-sm" style="background: var(--warning-light); color: var(--warning);">
                                <i class="bi bi-star-fill" style="font-size: 0.75rem;"></i>
                            </div>
                            <div>
                                <div style="font-size: 0.88rem; font-weight: 600;"><c:out value="${topper.name}" /></div>
                                <div style="font-size: 0.75rem; color: var(--text-muted);"><c:out value="${topper.rollNumber}" /></div>
                            </div>
                        </div>
                        <span class="badge badge--grade-aplus"><c:out value="${topper.averageMarks}" />% (<c:out value="${topper.grade}" />)</span>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
