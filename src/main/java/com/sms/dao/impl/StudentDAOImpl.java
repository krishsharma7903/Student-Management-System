package com.sms.dao.impl;

import com.sms.dao.StudentDAO;
import com.sms.exception.DatabaseException;
import com.sms.model.Student;
import com.sms.util.DBConnection;
import com.sms.util.ResultPage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of StudentDAO using PreparedStatement and clean transaction patterns.
 * 
 * // [JDBC] PreparedStatement only (prevents SQL injection)
 * // [JDBC] Batch insert for bulk student import
 * // [OOP] Polymorphism: compile-time overloading implementation
 */
public class StudentDAOImpl implements StudentDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentDAOImpl.class.getName());
    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "id", "roll_number", "first_name", "last_name", "department", "semester", "created_at"
    );

    private final DBConnection dbConnection;

    public StudentDAOImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    public StudentDAOImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean add(Student student) {
        String sql = "INSERT INTO students (roll_number, first_name, last_name, email, phone, " +
                     "date_of_birth, gender, department, semester, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStudentStatementParams(ps, student);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting student: " + student.getRollNumber(), e);
            throw new DatabaseException("Failed to add student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET roll_number = ?, first_name = ?, last_name = ?, " +
                     "email = ?, phone = ?, date_of_birth = ?, gender = ?, department = ?, " +
                     "semester = ?, address = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setStudentStatementParams(ps, student);
            ps.setInt(11, student.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating student ID: " + student.getId(), e);
            throw new DatabaseException("Failed to update student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student ID: " + id, e);
            throw new DatabaseException("Failed to delete student: " + e.getMessage(), e);
        }
    }

    @Override
    public Student findById(int id) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE s.id = ? GROUP BY s.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student by ID: " + id, e);
        }
    }

    @Override
    public Student findByRollNumber(String rollNumber) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE s.roll_number = ? GROUP BY s.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student by roll number: " + rollNumber, e);
        }
    }

    @Override
    public Student findByEmail(String email) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE LOWER(s.email) = LOWER(?) GROUP BY s.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student by email: " + email, e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "GROUP BY s.id ORDER BY s.id ASC";
        List<Student> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve students: " + e.getMessage(), e);
        }
    }

    @Override
    public ResultPage<Student> findPaginated(int page, int pageSize, String sortBy, String sortOrder) {
        int pageNo = Math.max(1, page);
        int limit = pageSize > 0 ? pageSize : 10;
        int offset = (pageNo - 1) * limit;

        // Whitelist sorting parameters to guarantee SQL injection safety
        String validatedSort = ALLOWED_SORT_COLUMNS.contains(sortBy != null ? sortBy.toLowerCase() : "") 
                ? sortBy : "id";
        String validatedOrder = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";

        String countSql = "SELECT COUNT(*) FROM students";
        String dataSql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                         "FROM students s " +
                         "LEFT JOIN enrollments e ON s.id = e.student_id " +
                         "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                         "GROUP BY s.id ORDER BY s." + validatedSort + " " + validatedOrder + " " +
                         "LIMIT ? OFFSET ?";

        List<Student> students = new ArrayList<>();
        long totalRecords = 0;

        try (Connection conn = dbConnection.getConnection()) {
            try (PreparedStatement countPs = conn.prepareStatement(countSql);
                 ResultSet countRs = countPs.executeQuery()) {
                if (countRs.next()) {
                    totalRecords = countRs.getLong(1);
                }
            }

            try (PreparedStatement dataPs = conn.prepareStatement(dataSql)) {
                dataPs.setInt(1, limit);
                dataPs.setInt(2, offset);
                try (ResultSet dataRs = dataPs.executeQuery()) {
                    while (dataRs.next()) {
                        students.add(mapRowToStudent(dataRs));
                    }
                }
            }
            return new ResultPage<>(students, pageNo, limit, totalRecords);
        } catch (SQLException e) {
            throw new DatabaseException("Pagination query failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> search(String keyword) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE s.roll_number LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ? " +
                     "OR s.email LIKE ? OR s.department LIKE ? " +
                     "GROUP BY s.id ORDER BY s.id ASC";
        List<Student> list = new ArrayList<>();
        String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Search failed for keyword: " + keyword, e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count students", e);
        }
    }

    // [JDBC] Batch insert for bulk student import (addBatch/executeBatch)
    @Override
    public int[] batchInsert(List<Student> students) {
        String sql = "INSERT INTO students (roll_number, first_name, last_name, email, phone, " +
                     "date_of_birth, gender, department, semester, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Student s : students) {
                setStudentStatementParams(ps, s);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            conn.commit();
            return results;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Batch insert failed, rolling back", e);
            throw new DatabaseException("Batch insert operation failed: " + e.getMessage(), e);
        }
    }

    // [OOP] Polymorphism: compile-time overloading method 1 (by ID)
    @Override
    public Student searchStudent(int id) {
        return findById(id);
    }

    // [OOP] Polymorphism: compile-time overloading method 2 (by Name)
    @Override
    public List<Student> searchStudent(String name) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "LEFT JOIN enrollments e ON s.id = e.student_id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE CONCAT(s.first_name, ' ', s.last_name) LIKE ? " +
                     "GROUP BY s.id";
        List<Student> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Overloaded searchStudent(String name) failed", e);
        }
    }

    // [OOP] Polymorphism: compile-time overloading method 3 (by course & semester)
    @Override
    public List<Student> searchStudent(String courseCode, int semester) {
        String sql = "SELECT s.*, COALESCE(AVG(m.total_marks), 0.0) AS avg_marks " +
                     "FROM students s " +
                     "INNER JOIN enrollments e ON s.id = e.student_id " +
                     "INNER JOIN courses c ON e.course_id = c.id " +
                     "LEFT JOIN marks m ON e.id = m.enrollment_id " +
                     "WHERE c.course_code = ? AND s.semester = ? " +
                     "GROUP BY s.id";
        List<Student> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ps.setInt(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Overloaded searchStudent(course, semester) failed", e);
        }
    }

    private void setStudentStatementParams(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getRollNumber());
        ps.setString(2, s.getFirstName());
        ps.setString(3, s.getLastName());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getPhone());
        if (s.getDateOfBirth() != null) {
            ps.setDate(6, Date.valueOf(s.getDateOfBirth()));
        } else {
            ps.setNull(6, Types.DATE);
        }
        ps.setString(7, s.getGender());
        ps.setString(8, s.getDepartment());
        ps.setInt(9, s.getSemester());
        ps.setString(10, s.getAddress());
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            s.setDateOfBirth(dob.toLocalDate());
        }

        s.setGender(rs.getString("gender"));
        s.setDepartment(rs.getString("department"));
        s.setSemester(rs.getInt("semester"));
        s.setAddress(rs.getString("address"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            s.setCreatedAt(ts.toLocalDateTime());
        }

        double avgMarks = rs.getDouble("avg_marks");
        s.setAverageMarks(avgMarks);
        return s;
    }
}
