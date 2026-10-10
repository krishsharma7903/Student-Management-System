package com.sms.model;

import java.util.Objects;

/**
 * Entity representing an Institutional Administrator.
 * Extends Person (Inheritance) and provides Admin-specific metadata.
 * 
 * // [OOP] Inheritance: Admin extends Person
 * // [OOP] Polymorphism: Runtime method overriding of getRole() and displayDetails()
 */
public class Admin extends Person {
    private static final long serialVersionUID = 1L;

    private String adminLevel;

    public Admin() {
        super();
    }

    public Admin(int id, String name, String email, String phone, String adminLevel) {
        super(id, name, email, phone);
        this.adminLevel = adminLevel;
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String getRole() {
        return "ADMIN";
    }

    // [OOP] Polymorphism: runtime method overriding
    @Override
    public String displayDetails() {
        return String.format("Admin: [Name: %s, Email: %s, Level: %s]",
                getName(), getEmail(), adminLevel);
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin admin)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(adminLevel, admin.adminLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adminLevel);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                '}';
    }
}
