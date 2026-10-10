package com.sms.servlet;

import com.sms.model.Course;
import com.sms.model.Enrollment;
import com.sms.model.Student;
import com.sms.service.CourseService;
import com.sms.service.EnrollmentService;
import com.sms.service.StudentService;
import com.sms.util.ResultPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller managing Student Course Enrollments.
 * 
 * // [WEB] EnrollmentServlet with PRG pattern
 */
@WebServlet("/enrollments")
public class EnrollmentServlet extends HttpServlet {

    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "new":
                showEnrollForm(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            case "status":
                handleStatusUpdate(req, resp);
                break;
            case "list":
            default:
                listEnrollments(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("status".equalsIgnoreCase(action)) {
            handleStatusUpdate(req, resp);
        } else {
            saveEnrollment(req, resp);
        }
    }

    private void listEnrollments(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        int pageSize = 10;
        try {
            String pStr = req.getParameter("page");
            if (pStr != null && !pStr.trim().isEmpty()) {
                page = Integer.parseInt(pStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        ResultPage<Enrollment> pageResult = enrollmentService.getPaginatedEnrollments(page, pageSize);
        req.setAttribute("pageResult", pageResult);
        req.setAttribute("enrollments", pageResult.getData());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", pageResult.getTotalPages());
        req.setAttribute("totalRecords", pageResult.getTotalRecords());

        req.getRequestDispatcher("/WEB-INF/views/enrollment-list.jsp").forward(req, resp);
    }

    private void showEnrollForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Student> students = studentService.getAllStudents();
        List<Course> courses = courseService.getAllCourses();

        req.setAttribute("students", students);
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/views/enrollment-form.jsp").forward(req, resp);
    }

    private void saveEnrollment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int studentId = Integer.parseInt(req.getParameter("studentId"));
            int courseId = Integer.parseInt(req.getParameter("courseId"));
            int semester = Integer.parseInt(req.getParameter("semester"));
            String academicYear = req.getParameter("academicYear");
            String status = req.getParameter("status");

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourseId(courseId);
            enrollment.setSemester(semester);
            enrollment.setAcademicYear(academicYear);
            enrollment.setStatus(status != null ? status : "ACTIVE");
            enrollment.setEnrollmentDate(LocalDate.now());

            enrollmentService.enrollStudent(enrollment);
            req.getSession().setAttribute("flashSuccess", "Student successfully enrolled in course!");
            resp.sendRedirect(req.getContextPath() + "/enrollments");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            showEnrollForm(req, resp);
        }
    }

    private void handleStatusUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String status = req.getParameter("status");
            enrollmentService.updateStatus(id, status);
            session.setAttribute("flashSuccess", "Enrollment status updated to " + status + ".");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to update enrollment: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/enrollments");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            enrollmentService.deleteEnrollment(id);
            session.setAttribute("flashSuccess", "Enrollment deleted successfully.");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to delete enrollment: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/enrollments");
    }
}
