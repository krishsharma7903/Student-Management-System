package com.sms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain entity representing an Academic Course.
 * Implements Comparable for natural sorting by course code and Reportable for analytics.
 * 
 * // [OOP] Encapsulation & Interface implementation
 * // [COLLECTIONS] Comparable for sorting
 */
public class Course implements Serializable, Comparable<Course>, Reportable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String courseCode;
    private String courseName;
    private int credits;
    private String department;
    private int semester;
    private int enrolledCount;
    private LocalDateTime createdAt;

    public Course() {
    }

    public Course(int id, String courseCode, String courseName, int credits, String department, int semester) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.semester = semester;
    }

    @Override
    public int compareTo(Course other) {
        if (other == null) return 1;
        if (this.courseCode == null && other.courseCode == null) return 0;
        if (this.courseCode == null) return -1;
        if (other.courseCode == null) return 1;
        return this.courseCode.compareToIgnoreCase(other.courseCode);
    }

    @Override
    public Map<String, Object> generateReportData() {
        Map<String, Object> map = new HashMap<>();
        map.put("courseCode", courseCode);
        map.put("courseName", courseName);
        map.put("department", department);
        map.put("credits", credits);
        map.put("enrolledCount", enrolledCount);
        return map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return id == course.id && Objects.equals(courseCode, course.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, courseCode);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", department='" + department + '\'' +
                ", semester=" + semester +
                ", enrolledCount=" + enrolledCount +
                '}';
    }
}
