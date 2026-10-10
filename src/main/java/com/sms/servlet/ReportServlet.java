package com.sms.servlet;

import com.sms.model.Student;
import com.sms.service.ReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controller compiling institutional academic reports and analytics.
 * 
 * // [WEB] ReportServlet (topper list, course enrollment, grade distribution, view reports)
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tab = req.getParameter("tab");
        if (tab == null || tab.trim().isEmpty()) {
            tab = "toppers";
        }

        req.setAttribute("activeTab", tab);

        // Fetch reporting datasets
        List<Student> overallToppers = reportService.getOverallToppers(10);
        List<Map<String, Object>> courseToppers = reportService.getCourseToppers();
        Map<String, Integer> courseEnrollments = reportService.getCourseEnrollments();
        Map<String, Long> gradeDist = reportService.getGradeDistribution();
        List<Map<String, Object>> resultView = reportService.getFullResultView();
        TreeMap<Double, List<Student>> rankList = reportService.getOrderedRankList();
        LinkedHashMap<String, Long> deptDistribution = reportService.getDepartmentStudentDistribution();

        req.setAttribute("overallToppers", overallToppers);
        req.setAttribute("courseToppers", courseToppers);
        req.setAttribute("courseEnrollments", courseEnrollments);
        req.setAttribute("gradeDistribution", gradeDist);
        req.setAttribute("resultView", resultView);
        req.setAttribute("rankList", rankList);
        req.setAttribute("deptDistribution", deptDistribution);

        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
    }
}
