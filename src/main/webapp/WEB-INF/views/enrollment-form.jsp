<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Enroll Student in Course" scope="request" />
<c:set var="activeMenu" value="enrollments" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card__header">
        <h2 class="card__title">
            <i class="bi bi-person-plus-fill" style="color: var(--primary);"></i>
            <span>Register Course Allocation</span>
        </h2>
        <a href="${pageContext.request.contextPath}/enrollments" class="btn btn-secondary btn-sm">
            <i class="bi bi-arrow-left"></i>
            <span>Back to Enrollments</span>
        </a>
    </div>

    <form action="${pageContext.request.contextPath}/enrollments" method="post">
        <div class="form-grid">
            <!-- Student Selector -->
            <div class="form-group form-group--full">
                <label for="studentId" class="form-label">Select Student <span class="required">*</span></label>
                <select id="studentId" name="studentId" class="form-control" required>
                    <option value="" disabled selected>-- Choose Student from Directory --</option>
                    <c:forEach var="s" items="${students}">
                        <option value="${s.id}">
                            <c:out value="${s.rollNumber}" /> - <c:out value="${s.name}" /> (<c:out value="${s.department}" />, Sem ${s.semester})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Course Selector -->
            <div class="form-group form-group--full">
                <label for="courseId" class="form-label">Select Course <span class="required">*</span></label>
                <select id="courseId" name="courseId" class="form-control" required>
                    <option value="" disabled selected>-- Choose Academic Course --</option>
                    <c:forEach var="c" items="${courses}">
                        <option value="${c.id}">
                            <c:out value="${c.courseCode}" />: <c:out value="${c.courseName}" /> (<c:out value="${c.credits}" /> Credits, Sem ${c.semester})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Semester -->
            <div class="form-group">
                <label for="semester" class="form-label">Enrollment Semester <span class="required">*</span></label>
                <select id="semester" name="semester" class="form-control" required>
                    <c:forEach begin="1" end="8" var="sem">
                        <option value="${sem}">Semester ${sem}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Academic Year -->
            <div class="form-group">
                <label for="academicYear" class="form-label">Academic Year <span class="required">*</span></label>
                <input type="text" id="academicYear" name="academicYear" class="form-control" 
                       value="2024-25" placeholder="e.g. 2024-25" required>
            </div>

            <!-- Status -->
            <div class="form-group form-group--full">
                <label for="status" class="form-label">Enrollment Status</label>
                <select id="status" name="status" class="form-control">
                    <option value="ACTIVE" selected>ACTIVE</option>
                    <option value="COMPLETED">COMPLETED</option>
                    <option value="DROPPED">DROPPED</option>
                </select>
            </div>
        </div>

        <div style="margin-top: 2rem; display: flex; justify-content: flex-end; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/enrollments" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-check2-circle"></i>
                <span>Complete Enrollment</span>
            </button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
