package com.sms.service;

import com.sms.dao.MarksDAO;
import com.sms.dao.impl.MarksDAOImpl;
import com.sms.exception.InvalidMarksException;
import com.sms.model.Marks;
import com.sms.util.ResultPage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service managing Academic Marks, Grade Computation, and Score Validation.
 * 
 * // [OOP] Custom Exception handling: InvalidMarksException
 * // [JDBC] Transactions for marks persistence
 */
public class MarksService {

    private final MarksDAO marksDAO;

    public MarksService() {
        this.marksDAO = new MarksDAOImpl();
    }

    public MarksService(MarksDAO marksDAO) {
        this.marksDAO = marksDAO;
    }

    public boolean saveOrUpdateMarks(Marks marks) {
        validateMarksRange(marks.getInternalMarks(), marks.getMidTermMarks(), marks.getEndTermMarks());
        return marksDAO.saveOrUpdate(marks);
    }

    public boolean updateMarksWithTransaction(int enrollmentId, double internal, double mid, double end, String remarks) {
        validateMarksRange(internal, mid, end);
        return marksDAO.updateMarksTransaction(enrollmentId, internal, mid, end, remarks);
    }

    public boolean deleteMarks(int id) {
        return marksDAO.delete(id);
    }

    public Marks getMarksById(int id) {
        Marks marks = marksDAO.findById(id);
        if (marks == null) {
            throw new IllegalArgumentException("Marks record with ID " + id + " not found.");
        }
        return marks;
    }

    public Marks getMarksByEnrollmentId(int enrollmentId) {
        return marksDAO.findByEnrollmentId(enrollmentId);
    }

    public List<Marks> getMarksByStudent(int studentId) {
        return marksDAO.findByStudentId(studentId);
    }

    public List<Marks> getMarksByCourse(int courseId) {
        return marksDAO.findByCourseId(courseId);
    }

    public List<Marks> getAllMarks() {
        return marksDAO.findAll();
    }

    public ResultPage<Marks> getPaginatedMarks(int page, int pageSize) {
        return marksDAO.findPaginated(page, pageSize);
    }

    public double getAverageMarks() {
        return marksDAO.getAverageMarks();
    }

    public Map<String, Long> getGradeDistribution() {
        return marksDAO.getGradeDistribution();
    }

    /**
     * Rigorous validation ensuring marks components adhere to academic regulations.
     * Throws InvalidMarksException if values exceed bounds.
     */
    public void validateMarksRange(double internal, double mid, double end) {
        if (internal < 0.0 || internal > 20.0) {
            throw new InvalidMarksException("Internal assessment marks must be between 0.0 and 20.0 (received: " + internal + ").");
        }
        if (mid < 0.0 || mid > 30.0) {
            throw new InvalidMarksException("Mid-term exam marks must be between 0.0 and 30.0 (received: " + mid + ").");
        }
        if (end < 0.0 || end > 50.0) {
            throw new InvalidMarksException("End-term exam marks must be between 0.0 and 50.0 (received: " + end + ").");
        }
        double total = internal + mid + end;
        if (total < 0.0 || total > 100.0) {
            throw new InvalidMarksException("Total aggregate score must not exceed 100.0 (received: " + total + ").");
        }
    }
}
