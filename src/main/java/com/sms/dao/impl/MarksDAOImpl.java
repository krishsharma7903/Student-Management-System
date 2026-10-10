package com.sms.dao.impl;

import com.sms.dao.MarksDAO;
import com.sms.exception.DatabaseException;
import com.sms.model.Marks;
import com.sms.util.DBConnection;
import com.sms.util.ResultPage;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of MarksDAO with complex SQL queries, views, subqueries, and transactions.
 * 
 * // [JDBC] PreparedStatement only
 * // [JDBC] Transactions: setAutoCommit(false), commit/rollback for marks updates
 * // [SQL] Subquery: topper per course
 * // [SQL] VIEW: v_student_results report view
 * // [SQL] GROUP BY + COUNT for grade distribution
 */
public class MarksDAOImpl implements MarksDAO {

    private static final Logger LOGGER = Logger.getLogger(MarksDAOImpl.class.getName());
    private final DBConnection dbConnection;

    public MarksDAOImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    public MarksDAOImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean saveOrUpdate(Marks marks) {
        marks.recalculateTotalAndGrade();
        String sql = "INSERT INTO marks (enrollment_id, internal_marks, mid_term_marks, end_term_marks, " +
                     "total_marks, grade, remarks) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE internal_marks = VALUES(internal_marks), " +
                     "mid_term_marks = VALUES(mid_term_marks), end_term_marks = VALUES(end_term_marks), " +
                     "total_marks = VALUES(total_marks), grade = VALUES(grade), remarks = VALUES(remarks)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, marks.getEnrollmentId());
            ps.setDouble(2, marks.getInternalMarks());
            ps.setDouble(3, marks.getMidTermMarks());
            ps.setDouble(4, marks.getEndTermMarks());
            ps.setDouble(5, marks.getTotalMarks());
            ps.setString(6, marks.getGrade());
            ps.setString(7, marks.getRemarks());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        marks.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed saving marks for enrollment ID " + marks.getEnrollmentId(), e);
            throw new DatabaseException("Failed to save marks: " + e.getMessage(), e);
        }
    }

    // [JDBC] Transactions: setAutoCommit(false), commit/rollback for marks updates
    @Override
    public boolean updateMarksTransaction(int enrollmentId, double internal, double mid, double end, String remarks) {
        double total = internal + mid + end;
        String grade;
        if (total >= 90.0) grade = "A+";
        else if (total >= 80.0) grade = "A";
        else if (total >= 70.0) grade = "B+";
        else if (total >= 60.0) grade = "B";
        else if (total >= 50.0) grade = "C";
        else grade = "F";

        String updateSql = "UPDATE marks SET internal_marks = ?, mid_term_marks = ?, end_term_marks = ?, " +
                           "total_marks = ?, grade = ?, remarks = ? WHERE enrollment_id = ?";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, internal);
                ps.setDouble(2, mid);
                ps.setDouble(3, end);
                ps.setDouble(4, total);
                ps.setString(5, grade);
                ps.setString(6, remarks);
                ps.setInt(7, enrollmentId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    // Try insert if not present
                    String insertSql = "INSERT INTO marks (enrollment_id, internal_marks, mid_term_marks, " +
                                       "end_term_marks, total_marks, grade, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        psIns.setInt(1, enrollmentId);
                        psIns.setDouble(2, internal);
                        psIns.setDouble(3, mid);
                        psIns.setDouble(4, end);
                        psIns.setDouble(5, total);
                        psIns.setString(6, grade);
                        psIns.setString(7, remarks);
                        psIns.executeUpdate();
                    }
                }
            }

            conn.commit(); // Commit transaction
            LOGGER.info("Marks update transaction committed successfully for enrollment " + enrollmentId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Marks transaction error. Rolling back...", e);
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.info("Transaction rollback completed.");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Rollback failed", ex);
                }
            }
            throw new DatabaseException("Marks update transaction failed and was rolled back: " + e.getMessage(), e);
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

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM marks WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete marks record: " + id, e);
        }
    }

    @Override
    public Marks findById(int id) {
        String sql = baseJoinQuery() + " WHERE m.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMarks(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch marks by ID: " + id, e);
        }
    }

    @Override
    public Marks findByEnrollmentId(int enrollmentId) {
        String sql = baseJoinQuery() + " WHERE m.enrollment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMarks(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch marks for enrollment ID: " + enrollmentId, e);
        }
    }

    @Override
    public List<Marks> findByStudentId(int studentId) {
        String sql = baseJoinQuery() + " WHERE e.student_id = ? ORDER BY c.course_code ASC";
        List<Marks> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMarks(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student marks", e);
        }
    }

    @Override
    public List<Marks> findByCourseId(int courseId) {
        String sql = baseJoinQuery() + " WHERE e.course_id = ? ORDER BY m.total_marks DESC";
        List<Marks> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMarks(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch course marks", e);
        }
    }

    @Override
    public List<Marks> findAll() {
        String sql = baseJoinQuery() + " ORDER BY m.id DESC";
        List<Marks> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToMarks(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all marks", e);
        }
    }

    @Override
    public ResultPage<Marks> findPaginated(int page, int pageSize) {
        int pageNo = Math.max(1, page);
        int limit = pageSize > 0 ? pageSize : 10;
        int offset = (pageNo - 1) * limit;

        String countSql = "SELECT COUNT(*) FROM marks";
        String dataSql = baseJoinQuery() + " ORDER BY m.id DESC LIMIT ? OFFSET ?";

        List<Marks> list = new ArrayList<>();
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
                        list.add(mapRowToMarks(rs));
                    }
                }
            }
            return new ResultPage<>(list, pageNo, limit, total);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to paginate marks", e);
        }
    }

    // [SQL] AVG aggregate function
    @Override
    public double getAverageMarks() {
        String sql = "SELECT COALESCE(AVG(total_marks), 0.0) FROM marks";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to compute average marks", e);
        }
    }

    // [SQL] GROUP BY + COUNT for grade distribution
    @Override
    public Map<String, Long> getGradeDistribution() {
        String sql = "SELECT grade, COUNT(*) AS count FROM marks GROUP BY grade " +
                     "ORDER BY FIELD(grade, 'A+', 'A', 'B+', 'B', 'C', 'F')";
        Map<String, Long> map = new LinkedHashMap<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("grade"), rs.getLong("count"));
            }
            return map;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch grade distribution", e);
        }
    }

    // [SQL] Correlated Subquery: Topper student per course
    @Override
    public List<Map<String, Object>> getTopperPerCourse() {
        String sql = "SELECT c.course_code, c.course_name, s.roll_number, " +
                     "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                     "m.total_marks, m.grade " +
                     "FROM marks m " +
                     "INNER JOIN enrollments e ON m.enrollment_id = e.id " +
                     "INNER JOIN students s ON e.student_id = s.id " +
                     "INNER JOIN courses c ON e.course_id = c.id " +
                     "WHERE m.total_marks = (" +
                     "    SELECT MAX(m2.total_marks) " +
                     "    FROM marks m2 " +
                     "    INNER JOIN enrollments e2 ON m2.enrollment_id = e2.id " +
                     "    WHERE e2.course_id = e.course_id" +
                     ") " +
                     "ORDER BY c.course_code ASC";

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("courseCode", rs.getString("course_code"));
                row.put("courseName", rs.getString("course_name"));
                row.put("rollNumber", rs.getString("roll_number"));
                row.put("studentName", rs.getString("student_name"));
                row.put("totalMarks", rs.getDouble("total_marks"));
                row.put("grade", rs.getString("grade"));
                list.add(row);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute topper subquery", e);
        }
    }

    // [SQL] VIEW: v_student_results report view
    @Override
    public List<Map<String, Object>> getResultViewReports() {
        String sql = "SELECT * FROM v_student_results ORDER BY roll_number, course_code";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("studentId", rs.getInt("student_id"));
                row.put("rollNumber", rs.getString("roll_number"));
                row.put("studentName", rs.getString("student_name"));
                row.put("email", rs.getString("email"));
                row.put("department", rs.getString("department"));
                row.put("courseCode", rs.getString("course_code"));
                row.put("courseName", rs.getString("course_name"));
                row.put("credits", rs.getInt("credits"));
                row.put("semester", rs.getInt("semester"));
                row.put("academicYear", rs.getString("academic_year"));
                row.put("enrollmentStatus", rs.getString("enrollment_status"));
                row.put("internalMarks", rs.getDouble("internal_marks"));
                row.put("midTermMarks", rs.getDouble("mid_term_marks"));
                row.put("endTermMarks", rs.getDouble("end_term_marks"));
                row.put("totalMarks", rs.getDouble("total_marks"));
                row.put("grade", rs.getString("grade"));
                row.put("remarks", rs.getString("remarks"));
                list.add(row);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query v_student_results view", e);
        }
    }

    private String baseJoinQuery() {
        return "SELECT m.*, " +
               "e.student_id, e.course_id, e.semester, e.academic_year, " +
               "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
               "s.roll_number, s.department AS student_department, " +
               "c.course_code, c.course_name, c.credits AS course_credits " +
               "FROM marks m " +
               "INNER JOIN enrollments e ON m.enrollment_id = e.id " +
               "INNER JOIN students s ON e.student_id = s.id " +
               "INNER JOIN courses c ON e.course_id = c.id";
    }

    private Marks mapRowToMarks(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setId(rs.getInt("id"));
        m.setEnrollmentId(rs.getInt("enrollment_id"));
        m.setInternalMarks(rs.getDouble("internal_marks"));
        m.setMidTermMarks(rs.getDouble("mid_term_marks"));
        m.setEndTermMarks(rs.getDouble("end_term_marks"));
        m.setTotalMarks(rs.getDouble("total_marks"));
        m.setGrade(rs.getString("grade"));
        m.setRemarks(rs.getString("remarks"));

        m.setStudentId(rs.getInt("student_id"));
        m.setStudentName(rs.getString("student_name"));
        m.setStudentRollNumber(rs.getString("roll_number"));
        m.setStudentDepartment(rs.getString("student_department"));
        m.setCourseId(rs.getInt("course_id"));
        m.setCourseCode(rs.getString("course_code"));
        m.setCourseName(rs.getString("course_name"));
        m.setCourseCredits(rs.getInt("course_credits"));
        m.setSemester(rs.getInt("semester"));
        m.setAcademicYear(rs.getString("academic_year"));

        return m;
    }
}
