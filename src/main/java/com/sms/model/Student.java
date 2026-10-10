package com.sms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Entity representing an enrolled Student.
 * Extends Person (Inheritance) and implements Gradable, Reportable, Comparable.
 * 
 * // [OOP] Inheritance: Student extends Person
 * // [OOP] Polymorphism: Runtime method overriding of getRole(), displayDetails(), calculateGrade()
 * // [OOP] Interface implementation: Gradable, Reportable, Comparable
 */
public class Student extends Person implements Gradable, Reportable, Comparable<Student> {
    private static final long serialVersionUID = 1L;

    private String rollNumber;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String department;
    private int semester;
    private String address;
    private LocalDateTime createdAt;
    
    // Aggregated / computed metrics for reporting & UI
    private double averageMarks;
    private String grade;

    public Student() {
        super();
    }

    public Student(int id, String rollNumber, String firstName, String lastName, 
                   String email, String phone, LocalDate dateOfBirth, String gender, 
                   String department, int semester, String address) {
        super(id, firstName + " " + lastName, email, phone);
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.department = department;
        this.semester = semester;
        this.address = address;
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String getRole() {
        return "STUDENT";
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String displayDetails() {
        return String.format("Student: [Roll: %s, Name: %s, Dept: %s, Sem: %d, Email: %s]",
                rollNumber, getName(), department, semester, getEmail());
    }

    // [OOP] Polymorphism: runtime method overriding from Gradable interface
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
        Map<String, Object> report = new HashMap<>();
        report.put("rollNumber", rollNumber);
        report.put("fullName", getName());
        report.put("department", department);
        report.put("semester", semester);
        report.put("averageMarks", averageMarks);
        report.put("overallGrade", grade != null ? grade : calculateGrade(averageMarks));
        return report;
    }

    // [COLLECTIONS] Comparable interface implementation for natural ordering by roll number
    @Override
    public int compareTo(Student o) {
        if (o == null) return 1;
        if (this.rollNumber == null && o.rollNumber == null) return 0;
        if (this.rollNumber == null) return -1;
        if (o.rollNumber == null) return 1;
        return this.rollNumber.compareToIgnoreCase(o.rollNumber);
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    private void updateFullName() {
        String f = firstName != null ? firstName : "";
        String l = lastName != null ? lastName : "";
        setName((f + " " + l).trim());
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageMarks() {
        return averageMarks;
    }

    public void setAverageMarks(double averageMarks) {
        this.averageMarks = averageMarks;
        this.grade = calculateGrade(averageMarks);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(rollNumber, student.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rollNumber);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + getName() + '\'' +
                ", department='" + department + '\'' +
                ", semester=" + semester +
                ", avgMarks=" + averageMarks +
                ", grade='" + grade + '\'' +
                '}';
    }
}
