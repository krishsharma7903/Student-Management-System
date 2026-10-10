package com.sms.servlet;

import com.sms.exception.DuplicateEmailException;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Enrollment;
import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.service.EnrollmentService;
import com.sms.service.MarksService;
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
 * Controller managing Student CRUD operations, search, pagination, and detailed academic profile views.
 * 
 * // [WEB] StudentServlet (list/add/edit/delete/search/sort/page) with PRG pattern
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final MarksService marksService = new MarksService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "new":
                showNewForm(req, resp);
                break;
            case "edit":
                showEditForm(req, resp);
                break;
            case "view":
                showStudentProfile(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            case "list":
            default:
                listStudents(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "save";
        }

        switch (action.toLowerCase()) {
            case "save":
                saveStudent(req, resp);
                break;
            case "update":
                updateStudent(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/students");
                break;
        }
    }

    private void listStudents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder");

        int page = 1;
        int pageSize = 8;

        try {
            String pStr = req.getParameter("page");
            if (pStr != null && !pStr.trim().isEmpty()) {
                page = Integer.parseInt(pStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "id";
        if (sortOrder == null || sortOrder.trim().isEmpty()) sortOrder = "ASC";

        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Student> searchResults = studentService.searchStudents(keyword.trim());
            req.setAttribute("students", searchResults);
            req.setAttribute("isSearch", true);
            req.setAttribute("keyword", keyword.trim());
            req.setAttribute("totalRecords", searchResults.size());
        } else {
            ResultPage<Student> pageResult = studentService.getPaginatedStudents(page, pageSize, sortBy, sortOrder);
            req.setAttribute("pageResult", pageResult);
            req.setAttribute("students", pageResult.getData());
            req.setAttribute("isSearch", false);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", pageResult.getTotalPages());
            req.setAttribute("totalRecords", pageResult.getTotalRecords());
        }

        req.setAttribute("sortBy", sortBy);
        req.setAttribute("sortOrder", sortOrder);
        req.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("student", new Student());
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Student student = studentService.getStudentById(id);
            req.setAttribute("student", student);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to retrieve student: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/students");
        }
    }

    private void showStudentProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Student student = studentService.getStudentById(id);
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(id);
            List<Marks> marks = marksService.getMarksByStudent(id);

            req.setAttribute("student", student);
            req.setAttribute("enrollments", enrollments);
            req.setAttribute("marksList", marks);
            req.getRequestDispatcher("/WEB-INF/views/student-view.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Student not found: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/students");
        }
    }

    private void saveStudent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = extractStudentFromRequest(req);
        try {
            studentService.addStudent(student);
            req.getSession().setAttribute("flashSuccess", "Student '" + student.getName() + "' registered successfully with Roll No: " + student.getRollNumber() + "!");
            // Post-Redirect-Get pattern
            resp.sendRedirect(req.getContextPath() + "/students");
        } catch (IllegalArgumentException | DuplicateEmailException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("student", student);
            req.setAttribute("isEdit", false);
            req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error saving student: " + e.getMessage());
            req.setAttribute("student", student);
            req.setAttribute("isEdit", false);
            req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
        }
    }

    private void updateStudent(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Student student = extractStudentFromRequest(req);
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            student.setId(id);
            studentService.updateStudent(student);
            req.getSession().setAttribute("flashSuccess", "Student record for '" + student.getName() + "' updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/students");
        } catch (IllegalArgumentException | DuplicateEmailException | StudentNotFoundException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("student", student);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error updating student: " + e.getMessage());
            req.setAttribute("student", student);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            studentService.deleteStudent(id);
            session.setAttribute("flashSuccess", "Student deleted successfully.");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to delete student: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/students");
    }

    private Student extractStudentFromRequest(HttpServletRequest req) {
        Student s = new Student();
        s.setRollNumber(req.getParameter("rollNumber"));
        s.setFirstName(req.getParameter("firstName"));
        s.setLastName(req.getParameter("lastName"));
        s.setEmail(req.getParameter("email"));
        s.setPhone(req.getParameter("phone"));

        String dobStr = req.getParameter("dateOfBirth");
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                s.setDateOfBirth(LocalDate.parse(dobStr.trim()));
            } catch (Exception ignored) {
            }
        }

        s.setGender(req.getParameter("gender"));
        s.setDepartment(req.getParameter("department"));

        try {
            s.setSemester(Integer.parseInt(req.getParameter("semester")));
        } catch (Exception e) {
            s.setSemester(1);
        }

        s.setAddress(req.getParameter("address"));
        return s;
    }
}
