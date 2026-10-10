<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="${isEdit ? 'Edit Academic Course' : 'Create New Course'}" scope="request" />
<c:set var="activeMenu" value="courses" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card__header">
        <h2 class="card__title">
            <i class="bi bi-journal-plus" style="color: var(--primary);"></i>
            <span><c:out value="${isEdit ? 'Update Course Specifications' : 'Define New Academic Course'}" /></span>
        </h2>
        <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary btn-sm">
            <i class="bi bi-arrow-left"></i>
            <span>Back to Catalog</span>
        </a>
    </div>

    <form action="${pageContext.request.contextPath}/courses" method="post">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'save'}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${course.id}">
        </c:if>

        <div class="form-grid">
            <div class="form-group">
                <label for="courseCode" class="form-label">Course Code <span class="required">*</span></label>
                <input type="text" id="courseCode" name="courseCode" class="form-control" 
                       placeholder="e.g. CS101" value="<c:out value='${course.courseCode}' />" required>
            </div>

            <div class="form-group">
                <label for="credits" class="form-label">Credit Units (1-10) <span class="required">*</span></label>
                <input type="number" id="credits" name="credits" class="form-control" 
                       min="1" max="10" value="<c:out value='${course.credits > 0 ? course.credits : 4}' />" required>
            </div>

            <div class="form-group form-group--full">
                <label for="courseName" class="form-label">Course Title <span class="required">*</span></label>
                <input type="text" id="courseName" name="courseName" class="form-control" 
                       placeholder="e.g. Data Structures and Algorithms" value="<c:out value='${course.courseName}' />" required>
            </div>

            <div class="form-group">
                <label for="department" class="form-label">Offering Department <span class="required">*</span></label>
                <select id="department" name="department" class="form-control" required>
                    <option value="" disabled ${empty course.department ? 'selected' : ''}>Select Department</option>
                    <option value="Computer Science" ${course.department == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                    <option value="Information Technology" ${course.department == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Electronics" ${course.department == 'Electronics' ? 'selected' : ''}>Electronics</option>
                    <option value="Mechanical" ${course.department == 'Mechanical' ? 'selected' : ''}>Mechanical</option>
                    <option value="Civil" ${course.department == 'Civil' ? 'selected' : ''}>Civil</option>
                </select>
            </div>

            <div class="form-group">
                <label for="semester" class="form-label">Curriculum Semester <span class="required">*</span></label>
                <select id="semester" name="semester" class="form-control" required>
                    <c:forEach begin="1" end="8" var="sem">
                        <option value="${sem}" ${course.semester == sem ? 'selected' : ''}>Semester ${sem}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div style="margin-top: 2rem; display: flex; justify-content: flex-end; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-check2-circle"></i>
                <span><c:out value="${isEdit ? 'Update Course' : 'Create Course'}" /></span>
            </button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
