<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Assessment Marks Evaluation" scope="request" />
<c:set var="activeMenu" value="marks" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="card__header">
        <h2 class="card__title">
            <i class="bi bi-award" style="color: var(--primary);"></i>
            <span>Evaluation & Grading Entry</span>
        </h2>
        <a href="${pageContext.request.contextPath}/marks" class="btn btn-secondary btn-sm">
            <i class="bi bi-arrow-left"></i>
            <span>Back to Marks</span>
        </a>
    </div>

    <!-- Context Info Banner -->
    <div style="background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.75rem; display: flex; justify-content: space-between; flex-wrap: wrap; gap: 1rem;">
        <div>
            <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Student Candidate</div>
            <div style="font-size: 1.05rem; font-weight: 700; margin-top: 0.2rem;"><c:out value="${enrollment.studentName}" /></div>
            <div style="font-size: 0.85rem; color: var(--primary); font-weight: 600;"><c:out value="${enrollment.studentRollNumber}" /></div>
        </div>
        <div>
            <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--text-muted); font-weight: 600;">Course Registered</div>
            <div style="font-size: 1.05rem; font-weight: 700; margin-top: 0.2rem;"><c:out value="${enrollment.courseCode}" />: <c:out value="${enrollment.courseName}" /></div>
            <div style="font-size: 0.85rem; color: var(--text-muted);">Semester <c:out value="${enrollment.semester}" /> (${enrollment.courseCredits} Credits)</div>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/marks" method="post">
        <input type="hidden" name="enrollmentId" value="${enrollment.id}">

        <div class="form-grid">
            <!-- Internal Assessment -->
            <div class="form-group">
                <label for="internalMarksInput" class="form-label">
                    Internal Assessment Marks (Max 20.0) <span class="required">*</span>
                </label>
                <input type="number" step="0.25" min="0" max="20" id="internalMarksInput" 
                       name="internalMarks" class="form-control" 
                       value="<c:out value='${marks.internalMarks}' />" required>
            </div>

            <!-- Mid-Term Exam -->
            <div class="form-group">
                <label for="midTermMarksInput" class="form-label">
                    Mid-Term Examination Marks (Max 30.0) <span class="required">*</span>
                </label>
                <input type="number" step="0.25" min="0" max="30" id="midTermMarksInput" 
                       name="midTermMarks" class="form-control" 
                       value="<c:out value='${marks.midTermMarks}' />" required>
            </div>

            <!-- End-Term Exam -->
            <div class="form-group">
                <label for="endTermMarksInput" class="form-label">
                    End-Term Final Marks (Max 50.0) <span class="required">*</span>
                </label>
                <input type="number" step="0.25" min="0" max="50" id="endTermMarksInput" 
                       name="endTermMarks" class="form-control" 
                       value="<c:out value='${marks.endTermMarks}' />" required>
            </div>

            <!-- Live Computed Assessment Box -->
            <div class="form-group" style="background: var(--card); border: 2px dashed var(--border); border-radius: var(--radius-md); padding: 1rem; display: flex; flex-direction: column; justify-content: center; align-items: center;">
                <div style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase; color: var(--text-muted);">Aggregate Score & Grade</div>
                <div style="font-size: 1.75rem; font-weight: 800; font-family: 'Poppins', sans-serif; color: var(--primary); margin: 0.25rem 0;">
                    <span id="calculatedTotalDisplay"><c:out value="${marks.totalMarks}" /></span> / 100
                </div>
                <div>
                    <c:set var="gClean" value="${fn:toLowerCase(fn:replace(marks.grade != null ? marks.grade : 'F', '+', 'plus'))}" />
                    <span id="calculatedGradeDisplay" class="badge badge--grade-${gClean}">
                        <c:out value="${marks.grade != null ? marks.grade : 'F'}" />
                    </span>
                </div>
            </div>

            <!-- Remarks -->
            <div class="form-group form-group--full">
                <label for="remarks" class="form-label">Evaluator Remarks / Performance Notes</label>
                <textarea id="remarks" name="remarks" class="form-control" 
                          placeholder="Provide performance feedback or assessment comments..."><c:out value="${marks.remarks}" /></textarea>
            </div>
        </div>

        <div style="margin-top: 2rem; display: flex; justify-content: flex-end; gap: 1rem;">
            <a href="${pageContext.request.contextPath}/marks" class="btn btn-secondary">Cancel</a>
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-save-fill"></i>
                <span>Save Assessment & Commit</span>
            </button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
