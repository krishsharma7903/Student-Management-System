package com.sms.dao.impl;

import com.sms.dao.EnrollmentDAO;
import com.sms.exception.DatabaseException;
import com.sms.model.Enrollment;
import com.sms.util.DBConnection;
import com.sms.util.ResultPage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of EnrollmentDAO managing student-course relationships.
 * Demonstrates ACID transaction processing with commit and rollback.
 * 
 * // [JDBC] Transactions: setAutoCommit(false), commit/rollback for enrollment + marks
 * // [SQL] INNER JOINs between students, enrollments, and courses
 */
public class EnrollmentDAOImpl implements EnrollmentDAO {

    private static final Logger LOGGER = Logger.getLogger(EnrollmentDAOImpl.class.getName());
    private final DBConnection dbConnection;

    public EnrollmentDAOImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    public EnrollmentDAOImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean enroll(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, semester, academic_year, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            ps.setInt(4, enrollment.getSemester());
            ps.setString(5, enrollment.getAcademicYear());
            ps.setString(6, enrollment.getStatus() != null ? enrollment.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        enrollment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed enrolling student ID " + enrollment.getStudentId(), e);
            throw new DatabaseException("Enrollment failed: " + e.getMessage(), e);
        }
    }

    // [JDBC] Transactions: setAutoCommit(false), commit/rollback for enrollment + marks updates
    @Override
    public boolean enrollWithInitialMarksTransaction(Enrollment enrollment, double internalMarks, 
                                                     double midTermMarks, double endTermMarks) {
        String enrollSql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, semester, academic_year, status) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";
        String marksSql = "INSERT INTO marks (enrollment_id, internal_marks, mid_term_marks, end_term_marks, total_marks, grade, remarks) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            // Start transaction
            conn.setAutoCommit(false);

            int enrollmentId;
            try (PreparedStatement psEnroll = conn.prepareStatement(enrollSql, Statement.RETURN_GENERATED_KEYS)) {
                psEnroll.setInt(1, enrollment.getStudentId());
                psEnroll.setInt(2, enrollment.getCourseId());
                psEnroll.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
                psEnroll.setInt(4, enrollment.getSemester());
                psEnroll.setString(5, enrollment.getAcademicYear());
                psEnroll.setString(6, enrollment.getStatus() != null ? enrollment.getStatus() : "ACTIVE");

                psEnroll.executeUpdate();
                try (ResultSet rs = psEnroll.getGeneratedKeys()) {
                    if (rs.next()) {
                        enrollmentId = rs.getInt(1);
                        enrollment.setId(enrollmentId);
                    } else {
                        throw new SQLException("Failed to obtain generated enrollment ID.");
                    }
                }
            }

            double total = internalMarks + midTermMarks + endTermMarks;
            String grade = calculateGrade(total);

            try (PreparedStatement psMarks = conn.prepareStatement(marksSql)) {
                psMarks.setInt(1, enrollmentId);
                psMarks.setDouble(2, internalMarks);
                psMarks.setDouble(3, midTermMarks);
                psMarks.setDouble(4, endTermMarks);
                psMarks.setDouble(5, total);
                psMarks.setString(6, grade);
                psMarks.setString(7, "Initial enrollment assessment");
                psMarks.executeUpdate();
            }

            // Commit atomic transaction
            conn.commit();
            LOGGER.info("Successfully committed enrollment and marks transaction for student " + enrollment.getStudentId());
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Transaction failed. Rolling back enrollment transaction...", e);
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.info("Transaction rolled back cleanly.");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            throw new DatabaseException("Enrollment transaction failed and was rolled back: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private String calculateGrade(double score) {
        if (score >= 90.0) return "A+";
        if (score >= 80.0) return "A";
        if (score >= 70.0) return "B+";
        if (score >= 60.0) return "B";
        if (score >= 50.0) return "C";
        return "F";
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE enrollments SET status = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update enrollment status ID: " + id, e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete enrollment ID: " + id, e);
        }
    }

    @Override
    public Enrollment findById(int id) {
        String sql = baseJoinQuery() + " WHERE e.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEnrollment(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch enrollment ID: " + id, e);
        }
    }

    @Override
    public Enrollment findByStudentAndCourse(int studentId, int courseId) {
        String sql = baseJoinQuery() + " WHERE e.student_id = ? AND e.course_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEnrollment(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check existing enrollment", e);
        }
    }

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        String sql = baseJoinQuery() + " WHERE e.student_id = ? ORDER BY e.id DESC";
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student enrollments", e);
        }
    }

    @Override
    public List<Enrollment> findByCourseId(int courseId) {
        String sql = baseJoinQuery() + " WHERE e.course_id = ? ORDER BY e.id DESC";
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEnrollment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch course enrollments", e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        String sql = baseJoinQuery() + " ORDER BY e.id DESC";
        List<Enrollment> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToEnrollment(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve enrollments", e);
        }
    }

    @Override
    public ResultPage<Enrollment> findPaginated(int page, int pageSize) {
        int pageNo = Math.max(1, page);
        int limit = pageSize > 0 ? pageSize : 10;
        int offset = (pageNo - 1) * limit;

        String countSql = "SELECT COUNT(*) FROM enrollments";
        String dataSql = baseJoinQuery() + " ORDER BY e.id DESC LIMIT ? OFFSET ?";

        List<Enrollment> list = new ArrayList<>();
        long total = 0;

        try (Connection conn = dbConnection.getConnection()) {
            try (PreparedStatement countPs = conn.prepareStatement(countSql);
                 ResultSet rs = countPs.executeQuery()) {
                if (rs.next()) {
                    total = rs.getLong(1);
                }
            }

            try (PreparedStatement dataPs = conn.prepareStatement(dataSql)) {
                dataPs.setInt(1, limit);
                dataPs.setInt(2, offset);
                try (ResultSet rs = dataPs.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapRowToEnrollment(rs));
                    }
                }
            }
            return new ResultPage<>(list, pageNo, limit, total);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to paginate enrollments", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM enrollments";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count enrollments", e);
        }
    }

    private String baseJoinQuery() {
        return "SELECT e.*, " +
               "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
               "s.roll_number, s.email AS student_email, " +
               "c.course_code, c.course_name, c.credits " +
               "FROM enrollments e " +
               "INNER JOIN students s ON e.student_id = s.id " +
               "INNER JOIN courses c ON e.course_id = c.id";
    }

    private Enrollment mapRowToEnrollment(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setCourseId(rs.getInt("course_id"));

        Date d = rs.getDate("enrollment_date");
        if (d != null) {
            e.setEnrollmentDate(d.toLocalDate());
        }

        e.setSemester(rs.getInt("semester"));
        e.setAcademicYear(rs.getString("academic_year"));
        e.setStatus(rs.getString("status"));

        e.setStudentName(rs.getString("student_name"));
        e.setStudentRollNumber(rs.getString("roll_number"));
        e.setStudentEmail(rs.getString("student_email"));
        e.setCourseCode(rs.getString("course_code"));
        e.setCourseName(rs.getString("course_name"));
        e.setCourseCredits(rs.getInt("credits"));

        return e;
    }
}
