package com.sms.dao;

import com.sms.model.Course;
import com.sms.util.ResultPage;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for Course operations.
 * 
 * // [OOP] Interface implementation: CourseDAO
 */
public interface CourseDAO {
    boolean add(Course course);
    boolean update(Course course);
    boolean delete(int id);
    Course findById(int id);
    Course findByCode(String courseCode);
    List<Course> findAll();
    ResultPage<Course> findPaginated(int page, int pageSize, String sortBy, String sortOrder);
    List<Course> search(String keyword);
    int count();
    Map<String, Integer> getCourseEnrollmentCounts();
}
