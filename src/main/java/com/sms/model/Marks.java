package com.sms.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain entity representing Academic Marks & Evaluation.
 * Implements Gradable for Grade calculations and Reportable for transcript generation.
 * 
 * // [OOP] Polymorphism: runtime method overriding
 * // [OOP] Interface implementation: Gradable, Reportable
 */
public class Marks implements Serializable, Gradable, Reportable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int enrollmentId;
    private double internalMarks; // Out of 20
    private double midTermMarks;  // Out of 30
    private double endTermMarks;  // Out of 50
    private double totalMarks;    // Out of 100
    private String grade;
    private String remarks;

    // Joined presentation fields
    private int studentId;
    private String studentName;
    private String studentRollNumber;
    private String studentDepartment;
    private int courseId;
    private String courseCode;
    private String courseName;
    private int courseCredits;
    private int semester;
    private String academicYear;

    public Marks() {
    }

    public Marks(int id, int enrollmentId, double internalMarks, double midTermMarks, 
                 double endTermMarks, String remarks) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.internalMarks = internalMarks;
        this.midTermMarks = midTermMarks;
        this.endTermMarks = endTermMarks;
        this.totalMarks = internalMarks + midTermMarks + endTermMarks;
        this.grade = calculateGrade(this.totalMarks);
        this.remarks = remarks;
    }

    // [OOP] Polymorphism: runtime method overriding from Gradable
    @Override
    public String calculateGrade(double score) {
        if (score >= 90.0) return "A+";
        if (score >= 80.0) return "A";
        if (score >= 70.0) return "B+";
        if (score >= 60.0) return "B";
        if (score >= 50.0) return "C";
        return "F";
    }

    // [OOP] Interface implementation: Reportable
    @Override
    public Map<String, Object> generateReportData() {
        Map<String, Object> map = new HashMap<>();
        map.put("studentRollNumber", studentRollNumber);
        map.put("studentName", studentName);
        map.put("courseCode", courseCode);
        map.put("courseName", courseName);
        map.put("internalMarks", internalMarks);
        map.put("midTermMarks", midTermMarks);
        map.put("endTermMarks", endTermMarks);
        map.put("totalMarks", totalMarks);
        map.put("grade", grade);
        map.put("remarks", remarks);
        return map;
    }

    public void recalculateTotalAndGrade() {
        this.totalMarks = this.internalMarks + this.midTermMarks + this.endTermMarks;
        this.grade = calculateGrade(this.totalMarks);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public double getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(double internalMarks) {
        this.internalMarks = internalMarks;
        recalculateTotalAndGrade();
    }

    public double getMidTermMarks() {
        return midTermMarks;
    }

    public void setMidTermMarks(double midTermMarks) {
        this.midTermMarks = midTermMarks;
        recalculateTotalAndGrade();
    }

    public double getEndTermMarks() {
        return endTermMarks;
    }

    public void setEndTermMarks(double endTermMarks) {
        this.endTermMarks = endTermMarks;
        recalculateTotalAndGrade();
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
        this.grade = calculateGrade(totalMarks);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Marks marks)) return false;
        return id == marks.id || enrollmentId == marks.enrollmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enrollmentId);
    }

    @Override
    public String toString() {
        return "Marks{" +
                "enrollmentId=" + enrollmentId +
                ", student=" + studentRollNumber +
                ", course=" + courseCode +
                ", totalMarks=" + totalMarks +
                ", grade='" + grade + '\'' +
                '}';
    }
}
