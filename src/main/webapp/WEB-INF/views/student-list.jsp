<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Students Directory" scope="request" />
<c:set var="activeMenu" value="students" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card">
    <!-- Toolbar: Search and Action Buttons -->
    <div class="toolbar">
        <form action="${pageContext.request.contextPath}/students" method="get" class="search-box">
            <input type="hidden" name="action" value="list">
            <i class="bi bi-search" style="color: var(--text-muted);"></i>
            <input type="text" name="keyword" placeholder="Search by name, roll, email, or dept..." 
                   value="<c:out value='${param.keyword}' />">
            <c:if test="${not empty param.keyword}">
                <a href="${pageContext.request.contextPath}/students" style="color: var(--text-muted); font-size: 0.9rem;" title="Clear search">
                    <i class="bi bi-x-circle-fill"></i>
                </a>
            </c:if>
        </form>

        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/students?action=new" class="btn btn-primary">
                <i class="bi bi-person-plus-fill"></i>
                <span>Add Student</span>
            </a>
        </div>
    </div>

    <!-- Students Data Table -->
    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>
                        <a href="${pageContext.request.contextPath}/students?sortBy=roll_number&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Roll Number <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>
                        <a href="${pageContext.request.contextPath}/students?sortBy=first_name&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Student Name <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>
                        <a href="${pageContext.request.contextPath}/students?sortBy=department&sortOrder=${sortOrder == 'ASC' ? 'DESC' : 'ASC'}" style="color: inherit;">
                            Department <i class="bi bi-arrow-down-up" style="font-size: 0.75rem;"></i>
                        </a>
                    </th>
                    <th>Semester</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${students}">
                    <tr>
                        <td>
                            <strong style="color: var(--primary);"><c:out value="${s.rollNumber}" /></strong>
                        </td>
                        <td>
                            <div class="table-user-cell">
                                <div class="avatar-sm">
                                    <c:out value="${fn:substring(s.name, 0, 1)}" />
                                </div>
                                <div>
                                    <div style="font-weight: 600;"><c:out value="${s.name}" /></div>
                                    <div style="font-size: 0.75rem; color: var(--text-muted);"><c:out value="${s.gender}" /></div>
                                </div>
                            </div>
                        </td>
                        <td><c:out value="${s.email}" /></td>
                        <td><c:out value="${s.phone != null ? s.phone : 'N/A'}" /></td>
                        <td>
                            <span class="badge" style="background: var(--bg); border: 1px solid var(--border); color: var(--text);">
                                <c:out value="${s.department}" />
                            </span>
                        </td>
                        <td>Sem <c:out value="${s.semester}" /></td>
                        <td>
                            <div class="actions-cell">
                                <a href="${pageContext.request.contextPath}/students?action=view&id=${s.id}" 
                                   class="btn-icon-pill" title="View Profile">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/students?action=edit&id=${s.id}" 
                                   class="btn-icon-pill" title="Edit Student">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <c:if test="${sessionScope.user.admin}">
                                    <a href="${pageContext.request.contextPath}/students?action=delete&id=${s.id}" 
                                       class="btn-icon-pill btn--delete" title="Delete Student"
                                       data-confirm-delete data-item-name="${s.name} (${s.rollNumber})">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty students}">
                    <tr>
                        <td colspan="7" class="empty-state">
                            <i class="bi bi-people empty-state__icon"></i>
                            <div>No students found matching your criteria.</div>
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <!-- Pagination Footer -->
    <c:if test="${not isSearch && totalPages > 1}">
        <div class="pagination">
            <div class="pagination__info">
                Showing Page <strong><c:out value="${currentPage}" /></strong> of <strong><c:out value="${totalPages}" /></strong> 
                (Total <strong><c:out value="${totalRecords}" /></strong> students)
            </div>
            <div class="pagination__controls">
                <a href="${pageContext.request.contextPath}/students?page=${currentPage - 1}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                   class="page-btn ${currentPage <= 1 ? 'disabled' : ''}">
                    <i class="bi bi-chevron-left"></i>
                </a>
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/students?page=${p}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                       class="page-btn ${p == currentPage ? 'active' : ''}">
                        <c:out value="${p}" />
                    </a>
                </c:forEach>
                <a href="${pageContext.request.contextPath}/students?page=${currentPage + 1}&sortBy=${sortBy}&sortOrder=${sortOrder}" 
                   class="page-btn ${currentPage >= totalPages ? 'disabled' : ''}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
