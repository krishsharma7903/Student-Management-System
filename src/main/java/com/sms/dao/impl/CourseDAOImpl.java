package com.sms.dao.impl;

import com.sms.dao.CourseDAO;
import com.sms.exception.DatabaseException;
import com.sms.model.Course;
import com.sms.util.DBConnection;
import com.sms.util.ResultPage;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of CourseDAO using PreparedStatement and clean error handling.
 * 
 * // [JDBC] PreparedStatement only (prevents SQL injection)
 * // [SQL] GROUP BY + COUNT for course-wise enrollment metrics
 */
public class CourseDAOImpl implements CourseDAO {

    private static final Logger LOGGER = Logger.getLogger(CourseDAOImpl.class.getName());
    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "id", "course_code", "course_name", "credits", "department", "semester", "created_at"
    );

    private final DBConnection dbConnection;

    public CourseDAOImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    public CourseDAOImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean add(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department, semester) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getDepartment());
            ps.setInt(5, course.getSemester());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        course.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed inserting course: " + course.getCourseCode(), e);
            throw new DatabaseException("Failed to add course: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, " +
                     "department = ?, semester = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setInt(3, course.getCredits());
            ps.setString(4, course.getDepartment());
            ps.setInt(5, course.getSemester());
            ps.setInt(6, course.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course ID: " + course.getId(), e);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete course ID: " + id, e);
        }
    }

    @Override
    public Course findById(int id) {
        String sql = "SELECT c.*, COUNT(e.id) AS enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE c.id = ? GROUP BY c.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCourse(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by ID: " + id, e);
        }
    }

    @Override
    public Course findByCode(String courseCode) {
        String sql = "SELECT c.*, COUNT(e.id) AS enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE c.course_code = ? GROUP BY c.id";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCourse(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by code: " + courseCode, e);
        }
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT c.*, COUNT(e.id) AS enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "GROUP BY c.id ORDER BY c.id ASC";
        List<Course> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToCourse(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all courses", e);
        }
    }

    @Override
    public ResultPage<Course> findPaginated(int page, int pageSize, String sortBy, String sortOrder) {
        int pageNo = Math.max(1, page);
        int limit = pageSize > 0 ? pageSize : 10;
        int offset = (pageNo - 1) * limit;

        String validatedSort = ALLOWED_SORT_COLUMNS.contains(sortBy != null ? sortBy.toLowerCase() : "") 
                ? sortBy : "id";
        String validatedOrder = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";

        String countSql = "SELECT COUNT(*) FROM courses";
        String dataSql = "SELECT c.*, COUNT(e.id) AS enrolled_count " +
                         "FROM courses c " +
                         "LEFT JOIN enrollments e ON c.id = e.course_id " +
                         "GROUP BY c.id ORDER BY c." + validatedSort + " " + validatedOrder + " " +
                         "LIMIT ? OFFSET ?";

        List<Course> courses = new ArrayList<>();
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
                        courses.add(mapRowToCourse(dataRs));
                    }
                }
            }
            return new ResultPage<>(courses, pageNo, limit, totalRecords);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to paginate courses: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Course> search(String keyword) {
        String sql = "SELECT c.*, COUNT(e.id) AS enrolled_count " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "WHERE c.course_code LIKE ? OR c.course_name LIKE ? OR c.department LIKE ? " +
                     "GROUP BY c.id ORDER BY c.id ASC";
        List<Course> list = new ArrayList<>();
        String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCourse(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search courses with keyword: " + keyword, e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count courses", e);
        }
    }

    // [SQL] GROUP BY + COUNT for course-wise enrollment metrics
    @Override
    public Map<String, Integer> getCourseEnrollmentCounts() {
        String sql = "SELECT c.course_name, COUNT(e.id) AS total_enrolled " +
                     "FROM courses c " +
                     "LEFT JOIN enrollments e ON c.id = e.course_id " +
                     "GROUP BY c.id, c.course_name " +
                     "ORDER BY total_enrolled DESC";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("course_name"), rs.getInt("total_enrolled"));
            }
            return map;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve course enrollment statistics", e);
        }
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setCredits(rs.getInt("credits"));
        c.setDepartment(rs.getString("department"));
        c.setSemester(rs.getInt("semester"));
        c.setEnrolledCount(rs.getInt("enrolled_count"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(ts.toLocalDateTime());
        }
        return c;
    }
}
