package com.sms.dao;

import com.sms.model.Enrollment;
import com.sms.util.ResultPage;

import java.util.List;

/**
 * Data Access Object interface for Course Enrollment operations.
 * 
 * // [OOP] Interface implementation: EnrollmentDAO
 */
public interface EnrollmentDAO {
    boolean enroll(Enrollment enrollment);
    boolean enrollWithInitialMarksTransaction(Enrollment enrollment, double internalMarks, double midTermMarks, double endTermMarks);
    boolean updateStatus(int id, String status);
    boolean delete(int id);
    Enrollment findById(int id);
    Enrollment findByStudentAndCourse(int studentId, int courseId);
    List<Enrollment> findByStudentId(int studentId);
    List<Enrollment> findByCourseId(int courseId);
    List<Enrollment> findAll();
    ResultPage<Enrollment> findPaginated(int page, int pageSize);
    int count();
}
