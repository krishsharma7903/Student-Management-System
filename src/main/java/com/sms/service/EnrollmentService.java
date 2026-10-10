package com.sms.service;

import com.sms.dao.EnrollmentDAO;
import com.sms.dao.impl.EnrollmentDAOImpl;
import com.sms.model.Enrollment;
import com.sms.util.ResultPage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Service managing Course Enrollments and bridge transaction operations.
 */
public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
    }

    public EnrollmentService(EnrollmentDAO enrollmentDAO) {
        this.enrollmentDAO = enrollmentDAO;
    }

    public boolean enrollStudent(Enrollment enrollment) {
        validateEnrollment(enrollment);

        Enrollment existing = enrollmentDAO.findByStudentAndCourse(enrollment.getStudentId(), enrollment.getCourseId());
        if (existing != null) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDate.now());
        }
        return enrollmentDAO.enroll(enrollment);
    }

    public boolean enrollWithInitialMarks(Enrollment enrollment, double internalMarks, 
                                          double midTermMarks, double endTermMarks) {
        validateEnrollment(enrollment);

        Enrollment existing = enrollmentDAO.findByStudentAndCourse(enrollment.getStudentId(), enrollment.getCourseId());
        if (existing != null) {
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        if (enrollment.getEnrollmentDate() == null) {
            enrollment.setEnrollmentDate(LocalDate.now());
        }

        return enrollmentDAO.enrollWithInitialMarksTransaction(enrollment, internalMarks, midTermMarks, endTermMarks);
    }

    public boolean updateStatus(int id, String status) {
        if (status == null || (!status.equals("ACTIVE") && !status.equals("COMPLETED") && !status.equals("DROPPED"))) {
            throw new IllegalArgumentException("Status must be ACTIVE, COMPLETED, or DROPPED.");
        }
        return enrollmentDAO.updateStatus(id, status);
    }

    public boolean deleteEnrollment(int id) {
        return enrollmentDAO.delete(id);
    }

    public Enrollment getEnrollmentById(int id) {
        return enrollmentDAO.findById(id);
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        return enrollmentDAO.findByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return enrollmentDAO.findByCourseId(courseId);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentDAO.findAll();
    }

    public ResultPage<Enrollment> getPaginatedEnrollments(int page, int pageSize) {
        return enrollmentDAO.findPaginated(page, pageSize);
    }

    public int getEnrollmentCount() {
        return enrollmentDAO.count();
    }

    private void validateEnrollment(Enrollment enrollment) {
        Objects.requireNonNull(enrollment, "Enrollment instance cannot be null.");
        if (enrollment.getStudentId() <= 0) {
            throw new IllegalArgumentException("Invalid student ID.");
        }
        if (enrollment.getCourseId() <= 0) {
            throw new IllegalArgumentException("Invalid course ID.");
        }
        if (enrollment.getSemester() < 1 || enrollment.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
        if (enrollment.getAcademicYear() == null || enrollment.getAcademicYear().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic year is required (e.g., 2024-25).");
        }
    }
}
