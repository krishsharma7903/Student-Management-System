<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Academic Courses Catalog" scope="request" />
<c:set var="activeMenu" value="courses" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card">
    <div class="toolbar">
        <form action="${pageContext.request.contextPath}/courses" method="get" class="search-box">
            <input type="hidden" name="action" value="list">
            <i class="bi bi-search" style="color: var(--text-muted);"></i>
            <input type="text" name="keyword" placeholder="Search by course code, title, or dept..." 
                   value="<c:out value='${param.keyword}' />">
            <c:if test="${not empty param.keyword}">
                <a href="${pageContext.request.contextPath}/courses" style="color: var(--text-muted); font-size: 0.9rem;" title="Clear search">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </c:if>
        </form>

        <c:if test="${sessionScope.user.admin}">
            <div style="display: flex; gap: 0.75rem;">
                <a href="${pageContext.request.contextPath}/courses?action=new" class="btn btn-primary">
                    <i class="bi bi-plus-lg"></i>
                    <span>Add New Course</span>
                </a>
            </div>
        </c:if>
    </div>

    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>
                        <a href="${pageContext.request.contextPath}/courses?sortBy=course_code&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Course Code <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>
                        <a href="${pageContext.request.contextPath}/courses?sortBy=course_name&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Course Title <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>Credits</th>
                    <th>
                        <a href="${pageContext.request.contextPath}/courses?sortBy=department&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Department <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>Semester</th>
                    <th>Enrolled</th>
                    <c:if test="${sessionScope.user.admin}">
                        <th>Actions</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${courses}">
                    <tr>
                        <td>
                            <strong style="color: var(--accent);"><c:out value="${c.courseCode}" /></strong>
                        </td>
                        <td>
                            <div style="font-weight: 600;"><c:out value="${c.courseName}" /></div>
                        </td>
                        <td>
                            <span class="badge" style="background: var(--bg); border: 1px solid var(--border); color: var(--text);">
                                <c:out value="${c.credits}" /> Credits
                            </span>
                        </td>
                        <td><c:out value="${c.department}" /></td>
                        <td>Semester <c:out value="${c.semester}" /></td>
                        <td>
                            <span class="badge" style="background: var(--primary-light); color: var(--primary);">
                                <c:out value="${c.enrolledCount}" /> Students
                            </span>
                        </td>
                        <c:if test="${sessionScope.user.admin}">
                            <td>
                                <div class="actions-cell">
                                    <a href="${pageContext.request.contextPath}/courses?action=edit&id=${c.id}" 
                                       class="btn-icon-pill" title="Edit Course">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/courses?action=delete&id=${c.id}" 
                                       class="btn-icon-pill btn--delete" title="Delete Course"
                                       data-confirm-delete data-item-name="${c.courseCode} - ${c.courseName}">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </div>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <c:if test="${empty courses}">
                    <tr>
                        <td colspan="${sessionScope.user.admin ? 7 : 6}" class="empty-state">
                            <i class="bi bi-book empty-state__icon"></i>
                            <div>No courses found matching your criteria.</div>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <!-- Pagination -->
    <c:if test="${not isSearch && totalPages > 1}">
        <div class="pagination">
            <div class="pagination__info">
                Showing Page <strong><c:out value="${currentPage}" /></strong> of <strong><c:out value="${totalPages}" /></strong>
            </div>
            <div class="pagination__controls">
                <a href="${pageContext.request.contextPath}/courses?page=${currentPage - 1}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                   class="page-btn ${currentPage <= 1 ? 'disabled' : ''}">
                    <i class="bi bi-chevron-left"></i>
                </a>
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/courses?page=${p}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                       class="page-btn ${p == currentPage ? 'active' : ''}">
                        <c:out value="${p}" />
                    </a>
                </c:forEach>
                <a href="${pageContext.request.contextPath}/courses?page=${currentPage + 1}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                   class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
