<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Marks & Academic Grading" scope="request" />
<c:set var="activeMenu" value="marks" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card">
    <div class="toolbar">
        <div>
            <h2 style="font-size: 1.15rem; font-weight: 600;">Course Assessment Ledger</h2>
            <p style="font-size: 0.85rem; color: var(--text-muted);">Manage internal assessments, midterm exams, and end-semester evaluations</p>
        </div>
    </div>

    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Student</th>
                    <th>Course</th>
                    <th>Internal (20)</th>
                    <th>Mid-Term (30)</th>
                    <th>End-Term (50)</th>
                    <th>Total (100)</th>
                    <th>Grade</th>
                    <th>Remarks</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${marksList}">
                    <tr>
                        <td>
                            <div class="table-user-cell">
                                <div class="avatar-sm">
                                    <c:out value="${fn:substring(m.studentName, 0, 1)}" />
                                </div>
                                <div>
                                    <div style="font-weight: 600;"><c:out value="${m.studentName}" /></div>
                                    <div style="font-size: 0.75rem; color: var(--primary); font-weight: 600;"><c:out value="${m.studentRollNumber}" /></div>
                                </div>
                            </div>
                        </td>
                        <td>
                            <strong><c:out value="${m.courseCode}" /></strong>
                            <div style="font-size: 0.75rem; color: var(--text-muted);"><c:out value="${m.courseName}" /></div>
                        </td>
                        <td><c:out value="${m.internalMarks}" /></td>
                        <td><c:out value="${m.midTermMarks}" /></td>
                        <td><c:out value="${m.endTermMarks}" /></td>
                        <td>
                            <strong style="color: var(--text); font-size: 1rem;"><c:out value="${m.totalMarks}" /></strong>
                        </td>
                        <td>
                            <c:set var="gClean" value="${fn:toLowerCase(fn:replace(m.grade, '+', 'plus'))}" />
                            <span class="badge badge--grade-${gClean}">
                                <c:out value="${m.grade}" />
                            </span>
                        </td>
                        <td>
                            <span style="font-size: 0.82rem; color: var(--text-muted);" title="${m.remarks}">
                                <c:out value="${fn:substring(m.remarks != null ? m.remarks : 'Graded', 0, 30)}" />
                            </span>
                        </td>
                        <td>
                            <div class="actions-cell">
                                <a href="${pageContext.request.contextPath}/marks?action=edit&enrollmentId=${m.enrollmentId}" 
                                   class="btn-icon-pill" title="Update Marks">
                                    <i class="bi bi-pencil-square"></i>
                                </a>
                                <c:if test="${sessionScope.user.admin}">
                                    <a href="${pageContext.request.contextPath}/marks?action=delete&id=${m.id}" 
                                       class="btn-icon-pill btn--delete" title="Delete Marks"
                                       data-confirm-delete data-item-name="marks record for ${m.studentName}">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty marksList}">
                    <tr>
                        <td colspan="9" class="empty-state">
                            <i class="bi bi-award empty-state__icon"></i>
                            <div>No marks records found.</div>
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
                <a href="${pageContext.request.contextPath}/marks?page=${currentPage - 1}" 
                   class="page-btn ${currentPage <= 1 ? 'disabled' : ''}">
                    <i class="bi bi-chevron-left"></i>
                </a>
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/marks?page=${p}" 
                       class="page-btn ${p == currentPage ? 'active' : ''}">
                        <c:out value="${p}" />
                    </a>
                </c:forEach>
                <a href="${pageContext.request.contextPath}/marks?page=${currentPage + 1}" 
                   class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
