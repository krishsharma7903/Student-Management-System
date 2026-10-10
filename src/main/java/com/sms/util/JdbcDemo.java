package com.sms.util;

import com.sms.model.Admin;
import com.sms.model.Person;
import com.sms.model.Student;
import com.sms.model.Teacher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Standalone console application demonstrating plain JDBC operations:
 * Connect -> Insert -> Select -> Update -> Delete (CRUD),
 * and OOP Polymorphic reference demonstrations.
 * 
 * Run with: mvn exec:java -Dexec.mainClass="com.sms.util.JdbcDemo"
 * 
 * // [JDBC] Standalone console class JdbcDemo demonstrating connect -> insert -> select -> update -> delete
 * // [OOP] Polymorphism demo: Person reference holding Student, Teacher, Admin objects
 */
public class JdbcDemo {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   STUDENT MANAGEMENT SYSTEM - JDBC & OOP ARCHITECTURE DEMO");
        System.out.println("================================================================================\n");

        // ---------------------------------------------------------------------
        // PART 1: OOP POLYMORPHISM DEMONSTRATION
        // [OOP] Polymorphism: runtime method overriding via Person reference
        // ---------------------------------------------------------------------
        System.out.println(">>> [1] DEMONSTRATING OOP POLYMORPHISM (Person references):");
        
        List<Person> institutionalActors = new ArrayList<>();
        
        Person p1 = new Student(101, "SMS-DEMO-001", "Kavya", "Sharma", "kavya@demo.com", 
                                "9988776655", LocalDate.of(2004, 3, 15), "Female", "Computer Science", 2, "Demo Street");
        Person p2 = new Teacher(201, "Dr. Ramesh Gupta", "gupta@demo.com", "9876543210", 
                                "FAC-CS-09", "Computer Science", "Associate Professor");
        Person p3 = new Admin(301, "Suresh Verma", "admin@demo.com", "9876500000", "Super Administrator");

        institutionalActors.add(p1);
        institutionalActors.add(p2);
        institutionalActors.add(p3);

        for (Person person : institutionalActors) {
            // Runtime dynamic method dispatch based on actual runtime object type
            System.out.println("Role: " + person.getRole() + " | Details: " + person.displayDetails());
        }
        System.out.println("\nGeneric printAll demonstration:");
        CollectionsUtil.printAll(institutionalActors);

        // ---------------------------------------------------------------------
        // PART 2: JDBC CRUD DEMONSTRATION (Connect -> Insert -> Select -> Update -> Delete)
        // [JDBC] Plain JDBC with PreparedStatement
        // ---------------------------------------------------------------------
        System.out.println("\n>>> [2] DEMONSTRATING PLAIN JDBC CRUD OPERATIONS:");

        DBConnection db = DBConnection.getInstance();
        String demoRoll = "DEMO-999";
        int generatedId = -1;

        try (Connection conn = db.getConnection()) {
            System.out.println("[JDBC STEP 1] Database Connection successfully established!");
            System.out.println("Connected to: " + db.getDbUrl());

            // A. INSERT
            System.out.println("\n[JDBC STEP 2: INSERT] Inserting temporary demo student record...");
            String insertSql = "INSERT INTO students (roll_number, first_name, last_name, email, phone, " +
                               "gender, department, semester, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                psInsert.setString(1, demoRoll);
                psInsert.setString(2, "Test");
                psInsert.setString(3, "Student");
                psInsert.setString(4, "test.student@jdbcdemo.com");
                psInsert.setString(5, "9112233445");
                psInsert.setString(6, "Male");
                psInsert.setString(7, "Computer Science");
                psInsert.setInt(8, 1);
                psInsert.setString(9, "JDBC Lab Sandbox");

                int rows = psInsert.executeUpdate();
                System.out.println("Insert executed. Rows affected: " + rows);

                try (ResultSet rs = psInsert.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        System.out.println("Generated Primary Key (id): " + generatedId);
                    }
                }
            }

            // B. SELECT
            System.out.println("\n[JDBC STEP 3: SELECT] Querying the newly inserted record...");
            String selectSql = "SELECT id, roll_number, first_name, last_name, email, department FROM students WHERE roll_number = ?";
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setString(1, demoRoll);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        System.out.printf("Retrieved Record -> ID: %d | Roll: %s | Name: %s %s | Email: %s | Dept: %s%n",
                                rs.getInt("id"),
                                rs.getString("roll_number"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("department"));
                    }
                }
            }

            // C. UPDATE
            System.out.println("\n[JDBC STEP 4: UPDATE] Updating student department to 'Artificial Intelligence'...");
            String updateSql = "UPDATE students SET department = ?, semester = ? WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, "Artificial Intelligence");
                psUpdate.setInt(2, 2);
                psUpdate.setInt(3, generatedId);
                int updateRows = psUpdate.executeUpdate();
                System.out.println("Update executed. Rows affected: " + updateRows);
            }

            // Verify update
            try (PreparedStatement psVerify = conn.prepareStatement(selectSql)) {
                psVerify.setString(1, demoRoll);
                try (ResultSet rs = psVerify.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Verified Updated Department: " + rs.getString("department"));
                    }
                }
            }

            // D. DELETE
            System.out.println("\n[JDBC STEP 5: DELETE] Cleaning up and deleting test record...");
            String deleteSql = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, generatedId);
                int delRows = psDelete.executeUpdate();
                System.out.println("Delete executed. Rows affected: " + delRows);
            }

            // Verify deletion
            try (PreparedStatement psVerifyDel = conn.prepareStatement(selectSql)) {
                psVerifyDel.setString(1, demoRoll);
                try (ResultSet rs = psVerifyDel.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Verified: Record no longer exists in database.");
                    }
                }
            }

            System.out.println("\n================================================================================");
            System.out.println("   DEMO COMPLETED SUCCESSFULLY: ALL JDBC & OOP VALIDATIONS PASSED");
            System.out.println("================================================================================");

        } catch (SQLException e) {
            System.err.println("JDBC Operation failed with SQLException: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
