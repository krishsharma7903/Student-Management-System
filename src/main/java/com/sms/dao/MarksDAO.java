package com.sms.dao;

import com.sms.model.Marks;
import com.sms.util.ResultPage;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Student Marks and Grade evaluations.
 * 
 * // [OOP] Interface implementation: MarksDAO
 */
public interface MarksDAO {
    boolean saveOrUpdate(Marks marks);
    boolean updateMarksTransaction(int enrollmentId, double internal, double mid, double end, String remarks);
    boolean delete(int id);
    Marks findById(int id);
    Marks findByEnrollmentId(int enrollmentId);
    List<Marks> findByStudentId(int studentId);
    List<Marks> findByCourseId(int courseId);
    List<Marks> findAll();
    ResultPage<Marks> findPaginated(int page, int pageSize);
    double getAverageMarks();
    Map<String, Long> getGradeDistribution();
    List<Map<String, Object>> getTopperPerCourse();
    List<Map<String, Object>> getResultViewReports();
}
