<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Course Enrollments" scope="request" />
<c:set var="activeMenu" value="enrollments" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card">
    <div class="toolbar">
        <div>
            <h2 style="font-size: 1.15rem; font-weight: 600;">Active Student-Course Mappings</h2>
            <p style="font-size: 0.85rem; color: var(--text-muted);">Manage semester registrations and course allocations</p>
        </div>

        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/enrollments?action=new" class="btn btn-primary">
                <i class="bi bi-person-plus-fill"></i>
                <span>Enroll Student</span>
            </a>
        </div>
    </div>

    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Student Details</th>
                    <th>Enrolled Course</th>
                    <th>Credits</th>
                    <th>Academic Year</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="e" items="${enrollments}">
                    <tr>
                        <td>
                            <div class="table-user-cell">
                                <div class="avatar-sm">
                                    <c:out value="${fn:substring(e.studentName, 0, 1)}" />
                                </div>
                                <div>
                                    <div style="font-weight: 600;"><c:out value="${e.studentName}" /></div>
                                    <div style="font-size: 0.78rem; color: var(--primary); font-weight: 600;">
                                        <c:out value="${e.studentRollNumber}" />
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div>
                                <strong><c:out value="${e.courseCode}" /></strong> - <c:out value="${e.courseName}" />
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">Semester <c:out value="${e.semester}" /></div>
                        </td>
                        <td><c:out value="${e.courseCredits}" /> Cr</td>
                        <td><c:out value="${e.academicYear}" /></td>
                        <td>
                            <c:set var="statusClean" value="${fn:toLowerCase(e.status)}" />
                            <span class="badge badge--status-${statusClean}">
                                <c:out value="${e.status}" />
                            </span>
                        </td>
                        <td>
                            <div class="actions-cell">
                                <a href="${pageContext.request.contextPath}/marks?action=edit&enrollmentId=${e.id}" 
                                   class="btn-icon-pill" title="Enter / Update Marks">
                                    <i class="bi bi-award"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/enrollments?action=delete&id=${e.id}" 
                                   class="btn-icon-pill btn--delete" title="Drop Enrollment"
                                   data-confirm-delete data-item-name="${e.studentName} from ${e.courseCode}">
                                    <i class="bi bi-x-circle"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty enrollments}">
                    <tr>
                        <td colspan="6" class="empty-state">
                            <i class="bi bi-person-x empty-state__icon"></i>
                            <div>No enrollments recorded.</div>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <div class="pagination__info">
                Showing Page <strong><c:out value="${currentPage}" /></strong> of <strong><c:out value="${totalPages}" /></strong>
            </div>
            <div class="pagination__controls">
                <a href="${pageContext.request.contextPath}/enrollments?page=${currentPage - 1}" 
                   class="page-btn ${currentPage <= 1 ? 'disabled' : ''}">
                    <i class="bi bi-chevron-left"></i>
                </a>
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/enrollments?page=${p}" 
                       class="page-btn ${p == currentPage ? 'active' : ''}">
                        <c:out value="${p}" />
                    </a>
                </c:forEach>
                <a href="${pageContext.request.contextPath}/enrollments?page=${currentPage + 1}" 
                   class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
