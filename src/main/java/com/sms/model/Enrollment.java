package com.sms.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain entity representing the Many-to-Many Enrollment bridge between Students and Courses.
 * 
 * // [OOP] Encapsulation & Interface implementation
 */
public class Enrollment implements Serializable, Reportable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private int semester;
    private String academicYear;
    private String status; // "ACTIVE", "COMPLETED", "DROPPED"

    // Joined presentation fields
    private String studentName;
    private String studentRollNumber;
    private String studentEmail;
    private String courseCode;
    private String courseName;
    private int courseCredits;

    public Enrollment() {
    }

    public Enrollment(int id, int studentId, int courseId, LocalDate enrollmentDate, 
                      int semester, String academicYear, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.academicYear = academicYear;
        this.status = status;
    }

    @Override
    public Map<String, Object> generateReportData() {
        Map<String, Object> map = new HashMap<>();
        map.put("enrollmentId", id);
        map.put("studentName", studentName);
        map.put("rollNumber", studentRollNumber);
        map.put("courseCode", courseCode);
        map.put("courseName", courseName);
        map.put("academicYear", academicYear);
        map.put("status", status);
        return map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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

    public int getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment that)) return false;
        return id == that.id || (studentId == that.studentId && courseId == that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, courseId);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", student=" + studentRollNumber + " (" + studentName + ")" +
                ", course=" + courseCode +
                ", semester=" + semester +
                ", status='" + status + '\'' +
                '}';
    }
}
