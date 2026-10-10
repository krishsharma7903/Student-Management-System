package com.sms.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class representing a human actor in the academic institution.
 * Demonstrates OOP Inheritance, Encapsulation, and Polymorphism.
 * 
 * // [OOP] Inheritance: abstract class Person
 * // [OOP] Encapsulation: private fields with standard accessors
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;
    private String phone;

    public Person() {
    }

    public Person(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // [OOP] Polymorphism: Abstract methods to be overridden by subclasses
    public abstract String getRole();
    public abstract String displayDetails();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return id == person.id && Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
