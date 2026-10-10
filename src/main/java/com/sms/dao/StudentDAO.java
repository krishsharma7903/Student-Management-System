package com.sms.dao;

import com.sms.model.Student;
import com.sms.util.ResultPage;

import java.util.List;

/**
 * Data Access Object interface for Student entity operations.
 * Demonstrates compile-time polymorphism (method overloading).
 * 
 * // [OOP] Interface implementation: StudentDAO
 * // [OOP] Polymorphism: Compile-time method overloading
 */
public interface StudentDAO {
    boolean add(Student student);
    boolean update(Student student);
    boolean delete(int id);
    Student findById(int id);
    Student findByRollNumber(String rollNumber);
    Student findByEmail(String email);
    List<Student> findAll();
    ResultPage<Student> findPaginated(int page, int pageSize, String sortBy, String sortOrder);
    List<Student> search(String keyword);
    int count();
    int[] batchInsert(List<Student> students);

    // [OOP] Polymorphism: compile-time method overloading
    Student searchStudent(int id);
    List<Student> searchStudent(String name);
    List<Student> searchStudent(String courseCode, int semester);
}
