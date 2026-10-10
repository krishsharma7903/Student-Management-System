package com.sms.service;

import com.sms.dao.CourseDAO;
import com.sms.dao.impl.CourseDAOImpl;
import com.sms.model.Course;
import com.sms.util.ResultPage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service managing Course curriculum and academic catalog business operations.
 */
public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAOImpl();
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public boolean addCourse(Course course) {
        validateCourseInput(course);

        Course existing = courseDAO.findByCode(course.getCourseCode().trim());
        if (existing != null) {
            throw new IllegalArgumentException("Course with code '" + course.getCourseCode() + "' already exists.");
        }
        return courseDAO.add(course);
    }

    public boolean updateCourse(Course course) {
        validateCourseInput(course);

        Course existing = courseDAO.findById(course.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Course with ID " + course.getId() + " does not exist.");
        }

        Course duplicateCode = courseDAO.findByCode(course.getCourseCode().trim());
        if (duplicateCode != null && duplicateCode.getId() != course.getId()) {
            throw new IllegalArgumentException("Course code '" + course.getCourseCode() + "' is used by another course.");
        }

        return courseDAO.update(course);
    }

    public boolean deleteCourse(int id) {
        Course existing = courseDAO.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }
        return courseDAO.delete(id);
    }

    public Course getCourseById(int id) {
        Course course = courseDAO.findById(id);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + id + " does not exist.");
        }
        return course;
    }

    public Course getCourseByCode(String code) {
        Course course = courseDAO.findByCode(code);
        if (course == null) {
            throw new IllegalArgumentException("Course with code " + code + " not found.");
        }
        return course;
    }

    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    public ResultPage<Course> getPaginatedCourses(int page, int pageSize, String sortBy, String sortOrder) {
        return courseDAO.findPaginated(page, pageSize, sortBy, sortOrder);
    }

    public List<Course> searchCourses(String keyword) {
        return courseDAO.search(keyword);
    }

    public int getCourseCount() {
        return courseDAO.count();
    }

    public Map<String, Integer> getCourseEnrollmentStats() {
        return courseDAO.getCourseEnrollmentCounts();
    }

    private void validateCourseInput(Course course) {
        Objects.requireNonNull(course, "Course instance cannot be null.");

        if (course.getCourseCode() == null || course.getCourseCode().trim().length() < 2) {
            throw new IllegalArgumentException("Course code must be at least 2 characters.");
        }
        if (course.getCourseName() == null || course.getCourseName().trim().length() < 3) {
            throw new IllegalArgumentException("Course title must be at least 3 characters.");
        }
        if (course.getCredits() < 1 || course.getCredits() > 10) {
            throw new IllegalArgumentException("Course credits must be between 1 and 10.");
        }
        if (course.getDepartment() == null || course.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (course.getSemester() < 1 || course.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
    }
}
