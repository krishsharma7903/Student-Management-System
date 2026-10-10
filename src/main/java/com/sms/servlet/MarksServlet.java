package com.sms.servlet;

import com.sms.exception.InvalidMarksException;
import com.sms.model.Enrollment;
import com.sms.model.Marks;
import com.sms.service.EnrollmentService;
import com.sms.service.MarksService;
import com.sms.util.ResultPage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller managing Student Marks entry, grade computation, and academic score updating.
 * 
 * // [WEB] MarksServlet with transaction processing and PRG pattern
 */
@WebServlet("/marks")
public class MarksServlet extends HttpServlet {

    private final MarksService marksService = new MarksService();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action.toLowerCase()) {
            case "edit":
            case "entry":
                showMarksForm(req, resp);
                break;
            case "delete":
                handleDelete(req, resp);
                break;
            case "list":
            default:
                listMarks(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("delete".equalsIgnoreCase(action)) {
            handleDelete(req, resp);
        } else {
            saveMarks(req, resp);
        }
    }

    private void listMarks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        int pageSize = 10;
        try {
            String pStr = req.getParameter("page");
            if (pStr != null && !pStr.trim().isEmpty()) {
                page = Integer.parseInt(pStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        ResultPage<Marks> pageResult = marksService.getPaginatedMarks(page, pageSize);
        req.setAttribute("pageResult", pageResult);
        req.setAttribute("marksList", pageResult.getData());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", pageResult.getTotalPages());
        req.setAttribute("totalRecords", pageResult.getTotalRecords());

        req.getRequestDispatcher("/WEB-INF/views/marks-list.jsp").forward(req, resp);
    }

    private void showMarksForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int enrollmentId = 0;
            String enrollParam = req.getParameter("enrollmentId");
            if (enrollParam != null && !enrollParam.isEmpty()) {
                enrollmentId = Integer.parseInt(enrollParam);
            } else {
                String marksIdParam = req.getParameter("id");
                if (marksIdParam != null && !marksIdParam.isEmpty()) {
                    Marks m = marksService.getMarksById(Integer.parseInt(marksIdParam));
                    enrollmentId = m.getEnrollmentId();
                }
            }

            Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
            if (enrollment == null) {
                req.getSession().setAttribute("flashError", "Associated enrollment record not found.");
                resp.sendRedirect(req.getContextPath() + "/marks");
                return;
            }

            Marks existingMarks = marksService.getMarksByEnrollmentId(enrollmentId);
            if (existingMarks == null) {
                existingMarks = new Marks();
                existingMarks.setEnrollmentId(enrollmentId);
            }

            req.setAttribute("enrollment", enrollment);
            req.setAttribute("marks", existingMarks);
            req.getRequestDispatcher("/WEB-INF/views/marks-form.jsp").forward(req, resp);

        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to load marks form: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/marks");
        }
    }

    private void saveMarks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int enrollmentId = 0;
        try {
            enrollmentId = Integer.parseInt(req.getParameter("enrollmentId"));
            double internal = Double.parseDouble(req.getParameter("internalMarks"));
            double mid = Double.parseDouble(req.getParameter("midTermMarks"));
            double end = Double.parseDouble(req.getParameter("endTermMarks"));
            String remarks = req.getParameter("remarks");

            // [JDBC] Transactional persistence of marks updates
            marksService.updateMarksWithTransaction(enrollmentId, internal, mid, end, remarks);

            req.getSession().setAttribute("flashSuccess", "Academic marks evaluated and updated successfully!");
            resp.sendRedirect(req.getContextPath() + "/marks");

        } catch (InvalidMarksException e) {
            req.setAttribute("errorMessage", e.getMessage());
            showMarksForm(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Please provide valid numerical marks values.");
            showMarksForm(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error saving marks: " + e.getMessage());
            showMarksForm(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            marksService.deleteMarks(id);
            session.setAttribute("flashSuccess", "Marks record deleted successfully.");
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to delete marks record: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/marks");
    }
}
