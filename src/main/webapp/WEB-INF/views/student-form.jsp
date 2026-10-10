<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="${isEdit ? 'Edit Student Profile' : 'Register New Student'}" scope="request" />
<c:set var="activeMenu" value="students" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card" style="max-width: 900px; margin: 0 auto;">
    <div class="card__header">
        <h2 class="card__title">
            <i class="bi bi-person-lines-fill" style="color: var(--primary);"></i>
            <span><c:out value="${isEdit ? 'Update Student Details' : 'Student Enrollment Information'}" /></span>
        </h2>
        <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary btn-sm">
            <i class="bi bi-arrow-left"></i>
            <span>Back to List</span>
        </a>
    </div>

    <form action="${pageContext.request.contextPath}/students" method="post">
        <input type="hidden" name="action" value="${isEdit ? 'update' : 'save'}">
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${student.id}">
        </c:if>

        <div class="form-grid">
            <!-- Roll Number -->
            <div class="form-group">
                <label for="rollNumber" class="form-label">
                    Roll Number <span style="font-size: 0.75rem; color: var(--text-muted);">(Auto-generated if left blank)</span>
                </label>
                <input type="text" id="rollNumber" name="rollNumber" class="form-control" 
                       placeholder="e.g. SMS-2024-001" value="<c:out value='${student.rollNumber}' />" 
                       ${isEdit ? 'readonly' : ''}>
            </div>

            <!-- Department -->
            <div class="form-group">
                <label for="department" class="form-label">Department <span class="required">*</span></label>
                <select id="department" name="department" class="form-control" required>
                    <option value="" disabled ${empty student.department ? 'selected' : ''}>Select Department</option>
                    <option value="Computer Science" ${student.department == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                    <option value="Information Technology" ${student.department == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Electronics" ${student.department == 'Electronics' ? 'selected' : ''}>Electronics</option>
                    <option value="Mechanical" ${student.department == 'Mechanical' ? 'selected' : ''}>Mechanical</option>
                    <option value="Civil" ${student.department == 'Civil' ? 'selected' : ''}>Civil</option>
                </select>
            </div>

            <!-- First Name -->
            <div class="form-group">
                <label for="firstName" class="form-label">First Name <span class="required">*</span></label>
                <input type="text" id="firstName" name="firstName" class="form-control" 
                       placeholder="Enter first name" value="<c:out value='${student.firstName}' />" required minlength="2">
            </div>

            <!-- Last Name -->
            <div class="form-group">
                <label for="lastName" class="form-label">Last Name <span class="required">*</span></label>
                <input type="text" id="lastName" name="lastName" class="form-control" 
                       placeholder="Enter last name" value="<c:out value='${student.lastName}' />" required minlength="2">
            </div>

            <!-- Email -->
            <div class="form-group">
                <label for="email" class="form-label">Email Address <span class="required">*</span></label>
                <input type="email" id="email" name="email" class="form-control" 
                       placeholder="student@example.com" value="<c:out value='${student.email}' />" required>
            </div>

            <!-- Phone -->
            <div class="form-group">
                <label for="phone" class="form-label">Phone Number</label>
                <input type="tel" id="phone" name="phone" class="form-control" 
                       placeholder="10-digit mobile number" value="<c:out value='${student.phone}' />">
            </div>

            <!-- Date of Birth -->
            <div class="form-group">
                <label for="dateOfBirth" class="form-label">Date of Birth</label>
                <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control" 
                       value="<c:out value='${student.dateOfBirth}' />">
            </div>

            <!-- Gender -->
            <div class="form-group">
                <label for="gender" class="form-label">Gender</label>
                <select id="gender" name="gender" class="form-control">
                    <option value="Male" ${student.gender == 'Male' ? 'selected' : ''}>Male</option>
                    <option value="Female" ${student.gender == 'Female' ? 'selected' : ''}>Female</option>
                    <option value="Other" ${student.gender == 'Other' ? 'selected' : ''}>Other</option>
                </select>
            </div>

            <!-- Semester -->
            <div class="form-group">
                <label for="semester" class="form-label">Current Semester <span class="required">*</span></label>
                <select id="semester" name="semester" class="form-control" required>
                    <c:forEach begin="1" end="8" var="sem">
                        <option value="${sem}" ${student.semester == sem ? 'selected' : ''}>Semester ${sem}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Address -->
            <div class="form-group form-group--full">
                <label for="address" class="form-label">Residential Address</label>
                <textarea id="address" name="address" class="form-control" 
                          placeholder="Street, City, State, PIN code..."><c:out value="${student.address}" /></textarea>
            </div>
        </div>

        <div style="margin-top: 2rem; display: flex; justify-content: flex-end; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-check2-circle"></i>
                <span><c:out value="${isEdit ? 'Update Student Record' : 'Register Student'}" /></span>
            </button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
