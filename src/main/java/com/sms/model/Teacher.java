package com.sms.model;

import java.util.Objects;

/**
 * Entity representing an Academic Faculty member / Teacher.
 * Extends Person (Inheritance) and overrides role and detail methods (Polymorphism).
 * 
 * // [OOP] Inheritance: Teacher extends Person
 * // [OOP] Polymorphism: Runtime method overriding of getRole() and displayDetails()
 */
public class Teacher extends Person {
    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String department;
    private String designation;

    public Teacher() {
        super();
    }

    public Teacher(int id, String name, String email, String phone, 
                   String employeeId, String department, String designation) {
        super(id, name, email, phone);
        this.employeeId = employeeId;
        this.department = department;
        this.designation = designation;
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String getRole() {
        return "TEACHER";
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String displayDetails() {
        return String.format("Teacher: [EmpID: %s, Name: %s, Dept: %s, Designation: %s]",
                employeeId, getName(), department, designation);
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher teacher)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(employeeId, teacher.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), employeeId);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + getName() + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}
