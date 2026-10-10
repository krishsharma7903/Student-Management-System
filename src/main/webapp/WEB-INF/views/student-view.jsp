<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Student Academic Profile" scope="request" />
<c:set var="activeMenu" value="students" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<!-- Profile Header Card -->
<div class="card" style="margin-bottom: 1.5rem;">
    <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1.5rem;">
        <div style="display: flex; align-items: center; gap: 1.25rem;">
            <div class="avatar" style="width: 70px; height: 70px; font-size: 1.85rem; border-radius: var(--radius-lg);">
                <c:out value="${fn:substring(student.name, 0, 1)}" />
            </div>
            <div>
                <h2 style="font-size: 1.6rem; font-weight: 700; margin-bottom: 0.2rem;"><c:out value="${student.name}" /></h2>
                <div style="display: flex; gap: 0.65rem; align-items: center; flex-wrap: wrap;">
                    <span class="badge" style="background: var(--primary-light); color: var(--primary); font-size: 0.85rem;">
                        <i class="bi bi-person-badge"></i> <c:out value="${student.rollNumber}" />
                    </span>
                    <span class="badge" style="background: var(--bg); border: 1px solid var(--border); color: var(--text);">
                        <c:out value="${student.department}" />
                    </span>
                    <span class="badge" style="background: var(--accent-light); color: #0F766E;">
                        Semester <c:out value="${student.semester}" />
                    </span>
                </div>
            </div>
        </div>

        <div style="display: flex; gap: 0.75rem;">
            <a href="${pageContext.request.contextPath}/students?action=edit&id=${student.id}" class="btn btn-secondary">
                <i class="bi bi-pencil"></i>
                <span>Edit Profile</span>
            </a>
            <a href="${pageContext.request.contextPath}/enrollments?action=new" class="btn btn-primary">
                <i class="bi bi-plus-circle"></i>
                <span>Enroll in Course</span>
            </a>
        </div>
    </div>

    <!-- Personal & Contact Info Grid -->
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-top: 1.75rem; padding-top: 1.5rem; border-top: 1px solid var(--border-light);">
        <div>
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Email Address</div>
            <div style="font-weight: 500; margin-top: 0.25rem;"><c:out value="${student.email}" /></div>
        </div>
        <div>
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Phone Number</div>
            <div style="font-weight: 500; margin-top: 0.25rem;"><c:out value="${student.phone != null ? student.phone : 'Not Provided'}" /></div>
        </div>
        <div>
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Date of Birth</div>
            <div style="font-weight: 500; margin-top: 0.25rem;"><c:out value="${student.dateOfBirth != null ? student.dateOfBirth : 'N/A'}" /></div>
        </div>
        <div>
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Gender</div>
            <div style="font-weight: 500; margin-top: 0.25rem;"><c:out value="${student.gender}" /></div>
        </div>
        <div>
            <div style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Address</div>
            <div style="font-weight: 500; margin-top: 0.25rem;"><c:out value="${student.address != null ? student.address : 'N/A'}" /></div>
        </div>
    </div>
</div>

<!-- Academic Evaluations & Marks Card -->
<div class="card">
    <div class="card__header">
        <h3 class="card__title">
            <i class="bi bi-award-fill" style="color: var(--warning);"></i>
            <span>Course Assessments & Grades</span>
        </h3>
        <span style="font-size: 0.85rem; color: var(--text-muted);">
            Enrolled in <strong><c:out value="${fn:length(enrollments)}" /></strong> courses
        </span>
    </div>

    <div class="table-responsive">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Course Code</th>
                    <th>Course Title</th>
                    <th>Credits</th>
                    <th>Internal (20)</th>
                    <th>Mid-Term (30)</th>
                    <th>End-Term (50)</th>
                    <th>Total (100)</th>
                    <th>Grade</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="marks" items="${marksList}">
                    <tr>
                        <td><strong><c:out value="${marks.courseCode}" /></strong></td>
                        <td><c:out value="${marks.courseName}" /></td>
                        <td><c:out value="${marks.courseCredits}" /> credits</td>
                        <td><c:out value="${marks.internalMarks}" /></td>
                        <td><c:out value="${marks.midTermMarks}" /></td>
                        <td><c:out value="${marks.endTermMarks}" /></td>
                        <td><strong><c:out value="${marks.totalMarks}" /></strong></td>
                        <td>
                            <c:set var="gClean" value="${fn:toLowerCase(fn:replace(marks.grade, '+', 'plus'))}" />
                            <span class="badge badge--grade-${gClean}">
                                <c:out value="${marks.grade}" />
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/marks?action=edit&enrollmentId=${marks.enrollmentId}" 
                               class="btn-icon-pill" title="Edit Assessment">
                                <i class="bi bi-pencil-square"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty marksList}">
                    <tr>
                        <td colspan="9" class="empty-state">
                            No assessment marks recorded for this student yet.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
