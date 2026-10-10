<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Reports & Institutional Analytics" scope="request" />
<c:set var="activeMenu" value="reports" scope="request" />
<jsp:include page="/WEB-INF/views/includes/header.jsp" />

<!-- Navigation Tabs -->
<div class="tabs-nav">
    <a href="${pageContext.request.contextPath}/reports?tab=toppers" 
       class="tab-link ${activeTab == 'toppers' ? 'active' : ''}">
        <i class="bi bi-trophy-fill"></i>
        <span>Topper Lists</span>
    </a>
    <a href="${pageContext.request.contextPath}/reports?tab=enrollment" 
       class="tab-link ${activeTab == 'enrollment' ? 'active' : ''}">
        <i class="bi bi-bar-chart-fill"></i>
        <span>Course Enrollments</span>
    </a>
    <a href="${pageContext.request.contextPath}/reports?tab=grades" 
       class="tab-link ${activeTab == 'grades' ? 'active' : ''}">
        <i class="bi bi-pie-chart-fill"></i>
        <span>Grade Distribution</span>
    </a>
    <a href="${pageContext.request.contextPath}/reports?tab=results" 
       class="tab-link ${activeTab == 'results' ? 'active' : ''}">
        <i class="bi bi-table"></i>
        <span>Database View Results</span>
    </a>
    <a href="${pageContext.request.contextPath}/reports?tab=rank" 
       class="tab-link ${activeTab == 'rank' ? 'active' : ''}">
        <i class="bi bi-list-ol"></i>
        <span>Rank List (TreeMap)</span>
    </a>
</div>

<!-- TAB 1: TOPPERS -->
<c:if test="${activeTab == 'toppers'}">
    <!-- Sub-section A: Course-wise Toppers via SQL Subquery -->
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-star-fill" style="color: var(--warning);"></i>
                <span>Course-Wise Highest Scorers (SQL Correlated Subquery)</span>
            </h2>
            <span class="badge badge--grade-aplus">Top Per Course</span>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Course Code</th>
                        <th>Course Title</th>
                        <th>Roll Number</th>
                        <th>Topper Student</th>
                        <th>Highest Score</th>
                        <th>Grade</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ct" items="${courseToppers}">
                        <tr>
                            <td><strong style="color: var(--accent);"><c:out value="${ct.courseCode}" /></strong></td>
                            <td><c:out value="${ct.courseName}" /></td>
                            <td><strong><c:out value="${ct.rollNumber}" /></strong></td>
                            <td><c:out value="${ct.studentName}" /></td>
                            <td><strong style="color: var(--primary);"><c:out value="${ct.totalMarks}" /> / 100</strong></td>
                            <td>
                                <c:set var="gClean" value="${fn:toLowerCase(fn:replace(ct.grade, '+', 'plus'))}" />
                                <span class="badge badge--grade-${gClean}"><c:out value="${ct.grade}" /></span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Sub-section B: Overall Top Performers via Java Streams -->
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-award-fill" style="color: var(--primary);"></i>
                <span>Institutional Top Performers (Java 17 Streams & Lambdas)</span>
            </h2>
            <span style="font-size: 0.85rem; color: var(--text-muted);">Sorted by aggregate GPA</span>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Roll Number</th>
                        <th>Student Name</th>
                        <th>Department</th>
                        <th>Semester</th>
                        <th>Average Score</th>
                        <th>Grade</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${overallToppers}" varStatus="status">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${status.index == 0}">
                                        <span class="avatar-sm" style="background: #FEF3C7; color: #D97706; font-weight: 700;">1</span>
                                    </c:when>
                                    <c:when test="${status.index == 1}">
                                        <span class="avatar-sm" style="background: #E5E7EB; color: #4B5563; font-weight: 700;">2</span>
                                    </c:when>
                                    <c:when test="${status.index == 2}">
                                        <span class="avatar-sm" style="background: #FFEDD5; color: #C2410C; font-weight: 700;">3</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="padding-left: 0.5rem; font-weight: 600;">${status.index + 1}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><strong><c:out value="${s.rollNumber}" /></strong></td>
                            <td><c:out value="${s.name}" /></td>
                            <td><c:out value="${s.department}" /></td>
                            <td>Sem <c:out value="${s.semester}" /></td>
                            <td><strong style="color: var(--primary);"><c:out value="${s.averageMarks}" />%</strong></td>
                            <td>
                                <c:set var="gClean" value="${fn:toLowerCase(fn:replace(s.grade, '+', 'plus'))}" />
                                <span class="badge badge--grade-${gClean}"><c:out value="${s.grade}" /></span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<!-- TAB 2: COURSE ENROLLMENT -->
<c:if test="${activeTab == 'enrollment'}">
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-bar-chart-steps" style="color: var(--accent);"></i>
                <span>Course-Wise Enrollment Statistics (SQL GROUP BY + COUNT)</span>
            </h2>
        </div>
        <div class="chart-container" style="max-width: 850px; margin: 1rem 0 2rem;">
            <c:forEach var="entry" items="${courseEnrollments}">
                <div class="chart-bar-item">
                    <div class="chart-bar-label">
                        <span><strong><c:out value="${entry.key}" /></strong></span>
                        <span><c:out value="${entry.value}" /> students enrolled</span>
                    </div>
                    <div class="chart-bar-track">
                        <div class="chart-bar-fill" style="width: ${entry.value * 12}%;"></div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>

<!-- TAB 3: GRADE DISTRIBUTION -->
<c:if test="${activeTab == 'grades'}">
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-pie-chart-fill" style="color: var(--info);"></i>
                <span>Institutional Grade Distribution Breakdown</span>
            </h2>
        </div>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1.25rem; margin-top: 1rem;">
            <c:forEach var="entry" items="${gradeDistribution}">
                <div style="background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1.25rem; text-align: center;">
                    <c:set var="gClean" value="${fn:toLowerCase(fn:replace(entry.key, '+', 'plus'))}" />
                    <span class="badge badge--grade-${gClean}" style="font-size: 1.1rem; padding: 0.35rem 0.85rem;">
                        <c:out value="${entry.key}" />
                    </span>
                    <div style="font-size: 1.85rem; font-weight: 700; margin: 0.75rem 0 0.25rem; font-family: 'Poppins', sans-serif;">
                        <c:out value="${entry.value}" />
                    </div>
                    <div style="font-size: 0.8rem; color: var(--text-muted);">Allocated Grades</div>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>

<!-- TAB 4: DATABASE VIEW RESULTS -->
<c:if test="${activeTab == 'results'}">
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-table" style="color: var(--primary);"></i>
                <span>Compiled Institutional Results (MySQL Database VIEW: v_student_results)</span>
            </h2>
            <span class="badge" style="background: var(--primary-light); color: var(--primary);">
                <c:out value="${fn:length(resultView)}" /> Records
            </span>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Roll No</th>
                        <th>Student Name</th>
                        <th>Department</th>
                        <th>Course</th>
                        <th>Internal</th>
                        <th>Mid</th>
                        <th>End</th>
                        <th>Total</th>
                        <th>Grade</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${resultView}">
                        <tr>
                            <td><strong><c:out value="${row.rollNumber}" /></strong></td>
                            <td><c:out value="${row.studentName}" /></td>
                            <td><c:out value="${row.department}" /></td>
                            <td><c:out value="${row.courseCode}" /></td>
                            <td><c:out value="${row.internalMarks}" /></td>
                            <td><c:out value="${row.midTermMarks}" /></td>
                            <td><c:out value="${row.endTermMarks}" /></td>
                            <td><strong><c:out value="${row.totalMarks}" /></strong></td>
                            <td>
                                <c:set var="gClean" value="${fn:toLowerCase(fn:replace(row.grade, '+', 'plus'))}" />
                                <span class="badge badge--grade-${gClean}"><c:out value="${row.grade}" /></span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<!-- TAB 5: RANK LIST VIA TREEMAP -->
<c:if test="${activeTab == 'rank'}">
    <div class="card">
        <div class="card__header">
            <h2 class="card__title">
                <i class="bi bi-list-ol" style="color: var(--primary);"></i>
                <span>Ordered Rank List (Java Collections: TreeMap&lt;Double, List&lt;Student&gt;&gt;)</span>
            </h2>
            <span style="font-size: 0.85rem; color: var(--text-muted);">Sorted in Natural Descending Order</span>
        </div>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Score Bracket</th>
                        <th>Students Count</th>
                        <th>Students Awarded</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${rankList}">
                        <tr>
                            <td>
                                <strong style="color: var(--primary); font-size: 1.05rem;"><c:out value="${entry.key}" />%</strong>
                            </td>
                            <td>
                                <span class="badge" style="background: var(--bg); border: 1px solid var(--border);">
                                    <c:out value="${fn:length(entry.value)}" /> student(s)
                                </span>
                            </td>
                            <td>
                                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                    <c:forEach var="st" items="${entry.value}">
                                        <span class="badge" style="background: var(--primary-light); color: var(--primary);">
                                            <c:out value="${st.name}" /> (<c:out value="${st.rollNumber}" />)
                                        </span>
                                    </c:forEach>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
