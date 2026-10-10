package com.sms.servlet;

import com.sms.model.Course;
import com.sms.service.CourseService;
import com.sms.util.ResultPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller managing academic Course catalog CRUD operations, search, and pagination.
 * 
 * // [WEB] CourseServlet with Post-Redirect-Get pattern
 */
@WebServlet("/courses")
public class CourseServlet extends HttpServlet {

    private final CourseService courseService = new CourseService();

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
            case "delete":
                handleDelete(req, resp);
                break;
            case "list":
            default:
                listCourses(req, resp);
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
                saveCourse(req, resp);
                break;
            case "update":
                updateCourse(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/courses");
                break;
        }
    }

    private void listCourses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            List<Course> searchResults = courseService.searchCourses(keyword.trim());
            req.setAttribute("courses", searchResults);
            req.setAttribute("isSearch", true);
            req.setAttribute("keyword", keyword.trim());
            req.setAttribute("totalRecords", searchResults.size());
        } else {
            ResultPage<Course> pageResult = courseService.getPaginatedCourses(page, pageSize, sortBy, sortOrder);
            req.setAttribute("pageResult", pageResult);
            req.setAttribute("courses", pageResult.getData());
            req.setAttribute("isSearch", false);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", pageResult.getTotalPages());
            req.setAttribute("totalRecords", pageResult.getTotalRecords());
        }

        req.setAttribute("sortBy", sortBy);
        req.setAttribute("sortOrder", sortOrder);
        req.getRequestDispatcher("/WEB-INF/views/course-list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("course", new Course());
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/views/course-form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Course course = courseService.getCourseById(id);
            req.setAttribute("course", course);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/course-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to load course: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    private void saveCourse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Course course = extractCourseFromRequest(req);
        try {
            courseService.addCourse(course);
            req.getSession().setAttribute("flashSuccess", "Course '" + course.getCourseName() + "' added successfully!");
            resp.sendRedirect(req.getContextPath() + "/courses");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("course", course);
            req.setAttribute("isEdit", false);
            req.getRequestDispatcher("/WEB-INF/views/course-form.jsp").forward(req, resp);
        }
    }

    private void updateCourse(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Course course = extractCourseFromRequest(req);
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            course.setId(id);
            courseService.updateCourse(course);
            req.getSession().setAttribute("flashSuccess", "Course '" + course.getCourseName() + "' updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/courses");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("course", course);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/course-form.jsp").forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            courseService.deleteCourse(id);
            session.setAttribute("flashSuccess", "Course deleted successfully.");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to delete course: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/courses");
    }

    private Course extractCourseFromRequest(HttpServletRequest req) {
        Course c = new Course();
        c.setCourseCode(req.getParameter("courseCode"));
        c.setCourseName(req.getParameter("courseName"));
        try {
            c.setCredits(Integer.parseInt(req.getParameter("credits")));
        } catch (Exception e) {
            c.setCredits(3);
        }
        c.setDepartment(req.getParameter("department"));
        try {
            c.setSemester(Integer.parseInt(req.getParameter("semester")));
        } catch (Exception e) {
            c.setSemester(1);
        }
        return c;
    }
}
