package com.sms.servlet;

import com.sms.model.Student;
import com.sms.service.EnrollmentService;
import com.sms.service.ReportService;
import com.sms.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller preparing analytical summary metrics and charts for the Main Dashboard view.
 * 
 * // [CONCURRENCY] Uses Future from background Callable execution
 * // [WEB] DashboardServlet with RequestDispatcher
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private final StudentService studentService = new StudentService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // [CONCURRENCY] Launch asynchronous metrics computation
            Future<Map<String, Object>> futureSummary = reportService.generateDashboardSummaryAsync();

            // Fetch recent 5 students for the dashboard table
            List<Student> allStudents = studentService.getAllStudents();
            int limit = Math.min(5, allStudents.size());
            List<Student> recentStudents = allStudents.subList(0, limit);

            int totalEnrollments = enrollmentService.getEnrollmentCount();

            // Await async task resolution
            Map<String, Object> summary = futureSummary.get();

            req.setAttribute("totalStudents", summary.get("totalStudents"));
            req.setAttribute("totalCourses", summary.get("totalCourses"));
            req.setAttribute("totalEnrollments", totalEnrollments);
            req.setAttribute("averageMarks", summary.get("averageMarks"));
            req.setAttribute("courseEnrollments", summary.get("courseEnrollments"));
            req.setAttribute("gradeDistribution", summary.get("gradeDistribution"));
            req.setAttribute("recentStudents", recentStudents);

            // Fetch institutional top 3 toppers for highlight cards
            List<Student> topStudents = reportService.getOverallToppers(3);
            req.setAttribute("topStudents", topStudents);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard metrics", e);
            req.setAttribute("totalStudents", studentService.getStudentCount());
            req.setAttribute("totalCourses", 0);
            req.setAttribute("totalEnrollments", 0);
            req.setAttribute("averageMarks", 0.0);
            req.setAttribute("recentStudents", Collections.emptyList());
        }

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
