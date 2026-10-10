package com.sms.service;

import com.sms.dao.CourseDAO;
import com.sms.dao.MarksDAO;
import com.sms.dao.StudentDAO;
import com.sms.dao.impl.CourseDAOImpl;
import com.sms.dao.impl.MarksDAOImpl;
import com.sms.dao.impl.StudentDAOImpl;
import com.sms.model.Student;
import com.sms.util.CollectionsUtil;
import com.sms.util.ThreadPoolManager;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service managing Academic Reporting, Analytics, and Asynchronous Report Computation.
 * 
 * // [CONCURRENCY] Callable + Future example with ExecutorService
 * // [COLLECTIONS] Streams, lambdas, Collectors.groupingBy, TreeMap, LinkedHashMap
 */
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final MarksDAO marksDAO;
    private final ThreadPoolManager threadPoolManager;

    public ReportService() {
        this.studentDAO = new StudentDAOImpl();
        this.courseDAO = new CourseDAOImpl();
        this.marksDAO = new MarksDAOImpl();
        this.threadPoolManager = ThreadPoolManager.getInstance();
    }

    public ReportService(StudentDAO studentDAO, CourseDAO courseDAO, MarksDAO marksDAO, ThreadPoolManager threadPoolManager) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.marksDAO = marksDAO;
        this.threadPoolManager = threadPoolManager;
    }

    /**
     * [CONCURRENCY] Submits an asynchronous Callable task to generate comprehensive dashboard metrics
     * via the background thread pool, returning a Future.
     * Demonstrates Callable + Future pattern to prevent blocking web request threads.
     */
    public Future<Map<String, Object>> generateDashboardSummaryAsync() {
        Callable<Map<String, Object>> dashboardTask = () -> {
            LOGGER.info(() -> "[Thread: " + Thread.currentThread().getName() + "] Compiling asynchronous dashboard metrics...");
            Map<String, Object> summary = new HashMap<>();

            int totalStudents = studentDAO.count();
            int totalCourses = courseDAO.count();
            double avgMarks = marksDAO.getAverageMarks();
            Map<String, Integer> enrollmentCounts = courseDAO.getCourseEnrollmentCounts();
            Map<String, Long> gradeDist = marksDAO.getGradeDistribution();

            summary.put("totalStudents", totalStudents);
            summary.put("totalCourses", totalCourses);
            summary.put("averageMarks", Math.round(avgMarks * 100.0) / 100.0);
            summary.put("courseEnrollments", enrollmentCounts);
            summary.put("gradeDistribution", gradeDist);

            return summary;
        };

        return threadPoolManager.submit(dashboardTask);
    }

    /**
     * [COLLECTIONS] Streams & Lambdas: Computes the Top N Academic Toppers.
     */
    public List<Student> getOverallToppers(int limit) {
        List<Student> students = studentDAO.findAll();
        return students.stream()
                .filter(s -> s.getAverageMarks() > 0.0)
                .sorted((s1, s2) -> Double.compare(s2.getAverageMarks(), s1.getAverageMarks()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * [SQL] Subquery: Retrieves the highest scoring student in each course.
     */
    public List<Map<String, Object>> getCourseToppers() {
        return marksDAO.getTopperPerCourse();
    }

    /**
     * [SQL] VIEW: Fetches all compiled student academic results from v_student_results view.
     */
    public List<Map<String, Object>> getFullResultView() {
        return marksDAO.getResultViewReports();
    }

    /**
     * [COLLECTIONS] Uses TreeMap to construct an ordered Rank List.
     */
    public TreeMap<Double, List<Student>> getOrderedRankList() {
        List<Student> students = studentDAO.findAll();
        return CollectionsUtil.buildRankList(students);
    }

    /**
     * [COLLECTIONS] Uses LinkedHashMap to preserve order for department-wise student distribution.
     */
    public LinkedHashMap<String, Long> getDepartmentStudentDistribution() {
        List<Student> students = studentDAO.findAll();
        return CollectionsUtil.getDepartmentWiseStudentCount(students);
    }

    /**
     * [SQL] Course-wise enrollment metrics for dashboard charts.
     */
    public Map<String, Integer> getCourseEnrollments() {
        return courseDAO.getCourseEnrollmentCounts();
    }

    /**
     * [SQL] Grade distribution breakdown (A+, A, B+, B, C, F).
     */
    public Map<String, Long> getGradeDistribution() {
        return marksDAO.getGradeDistribution();
    }
}
