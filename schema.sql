-- ============================================================
-- DATABASE SCHEMA: Student Management System (SMS)
-- DBMS: MySQL 8.0+
-- Database: student_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS student_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE student_db;

-- 1. DROP EXISTING OBJECTS (IN SAFE FK ORDER)
DROP VIEW IF EXISTS v_student_results;
DROP TABLE IF EXISTS marks;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

-- ============================================================
-- 2. TABLE: users (Authentication & Role-Based Access Control)
-- ============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'TEACHER') NOT NULL DEFAULT 'TEACHER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. TABLE: students (Core Student Directory)
-- ============================================================
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE,
    gender ENUM('Male', 'Female', 'Other') DEFAULT 'Male',
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL DEFAULT 1,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_roll (roll_number),
    INDEX idx_student_dept (department),
    INDEX idx_student_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. TABLE: courses (Academic Course Catalog)
-- ============================================================
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_course_code (course_code),
    INDEX idx_course_dept (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. TABLE: enrollments (Many-to-Many Bridge Table)
-- ============================================================
CREATE TABLE enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL,
    semester INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    status ENUM('ACTIVE', 'COMPLETED', 'DROPPED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uk_student_course UNIQUE (student_id, course_id),
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) 
        REFERENCES students(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_enroll_student (student_id),
    INDEX idx_enroll_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. TABLE: marks (Academic Assessment & Grades)
-- ============================================================
CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL UNIQUE,
    internal_marks DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    mid_term_marks DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    end_term_marks DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    total_marks DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    grade VARCHAR(5) NOT NULL DEFAULT 'F',
    remarks VARCHAR(255),
    CONSTRAINT fk_marks_enrollment FOREIGN KEY (enrollment_id) 
        REFERENCES enrollments(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_total_marks CHECK (total_marks >= 0.00 AND total_marks <= 100.00),
    INDEX idx_marks_grade (grade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. DATABASE VIEW: v_student_results (Report View)
-- ============================================================
CREATE VIEW v_student_results AS
SELECT 
    s.id AS student_id,
    s.roll_number,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    s.email,
    s.department,
    c.id AS course_id,
    c.course_code,
    c.course_name,
    c.credits,
    e.id AS enrollment_id,
    e.semester,
    e.academic_year,
    e.status AS enrollment_status,
    COALESCE(m.internal_marks, 0.00) AS internal_marks,
    COALESCE(m.mid_term_marks, 0.00) AS mid_term_marks,
    COALESCE(m.end_term_marks, 0.00) AS end_term_marks,
    COALESCE(m.total_marks, 0.00) AS total_marks,
    COALESCE(m.grade, 'N/A') AS grade,
    m.remarks
FROM students s
INNER JOIN enrollments e ON s.id = e.student_id
INNER JOIN courses c ON e.course_id = c.id
LEFT JOIN marks m ON e.id = m.enrollment_id;

-- ============================================================
-- 8. SAMPLE DATA INSERTS
-- ============================================================

-- Users: BCrypt hashes for 'admin123' and 'teacher123'
-- admin / admin123  -> $2a$10$w8uQZ7dY6mE0W3H6rCj0c.lqN712G5jRj34vY/hQ0s.2E0QzZ1kQ6
-- teacher / teacher123 -> $2a$10$1yK2c83tZ348QnSgHwY.4u8WbUqX73QZ/d80eD0M0v/G7qKzH4lfa
-- (Note: The application AuthService dynamically ensures and verifies BCrypt passwords)
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', '$2a$10$64G5q8aWq2h2WpQ2w56lK.0aC60O9GgVvA1b.K9L5p4W0a8u0K1e.', 'System Administrator', 'admin@sms.edu', 'ADMIN'),
('prof_sharma', '$2a$10$64G5q8aWq2h2WpQ2w56lK.0aC60O9GgVvA1b.K9L5p4W0a8u0K1e.', 'Prof. Rajesh Sharma', 'sharma@sms.edu', 'TEACHER'),
('prof_patel', '$2a$10$64G5q8aWq2h2WpQ2w56lK.0aC60O9GgVvA1b.K9L5p4W0a8u0K1e.', 'Dr. Ananya Patel', 'patel@sms.edu', 'TEACHER');

-- 6 Sample Courses
INSERT INTO courses (course_code, course_name, credits, department, semester) VALUES
('CS101', 'Data Structures & Algorithms', 4, 'Computer Science', 3),
('CS102', 'Database Management Systems', 4, 'Computer Science', 4),
('CS103', 'Object-Oriented Java Programming', 4, 'Computer Science', 4),
('IT201', 'Computer Networks & Security', 3, 'Information Technology', 5),
('EC301', 'Digital Electronics & Microprocessors', 3, 'Electronics', 3),
('AI401', 'Artificial Intelligence & Machine Learning', 4, 'Computer Science', 6);

-- 12 Sample Students
INSERT INTO students (roll_number, first_name, last_name, email, phone, date_of_birth, gender, department, semester, address) VALUES
('SMS-2024-001', 'Aarav', 'Sharma', 'aarav.sharma@example.com', '9876543210', '2003-05-14', 'Male', 'Computer Science', 4, 'Flat 402, Green Glen Layout, Bangalore'),
('SMS-2024-002', 'Diya', 'Verma', 'diya.verma@example.com', '9876543211', '2003-08-22', 'Female', 'Computer Science', 4, '12B, Rose Villa, MG Road, Pune'),
('SMS-2024-003', 'Rohan', 'Mehta', 'rohan.mehta@example.com', '9876543212', '2002-11-10', 'Male', 'Information Technology', 5, '78, Lake View Apartments, Hyderabad'),
('SMS-2024-004', 'Ananya', 'Iyer', 'ananya.iyer@example.com', '9876543213', '2004-02-19', 'Female', 'Computer Science', 3, '45, Palm Grove, Chennai'),
('SMS-2024-005', 'Kabir', 'Singh', 'kabir.singh@example.com', '9876543214', '2003-09-30', 'Male', 'Electronics', 3, '204, Sector 14, Chandigarh'),
('SMS-2024-006', 'Ishita', 'Gupta', 'ishita.gupta@example.com', '9876543215', '2003-04-12', 'Female', 'Computer Science', 4, '108, Civil Lines, Jaipur'),
('SMS-2024-007', 'Arjun', 'Reddy', 'arjun.reddy@example.com', '9876543216', '2002-12-05', 'Male', 'Computer Science', 6, '56, Jubilee Hills, Hyderabad'),
('SMS-2024-008', 'Sneha', 'Nair', 'sneha.nair@example.com', '9876543217', '2003-07-18', 'Female', 'Information Technology', 5, '89, Marine Drive, Kochi'),
('SMS-2024-009', 'Aditya', 'Chopra', 'aditya.chopra@example.com', '9876543218', '2003-01-25', 'Male', 'Electronics', 3, '14, Bandra West, Mumbai'),
('SMS-2024-010', 'Pooja', 'Deshmukh', 'pooja.deshmukh@example.com', '9876543219', '2004-06-11', 'Female', 'Computer Science', 3, '301, Shivaji Nagar, Nagpur'),
('SMS-2024-011', 'Vikram', 'Malhotra', 'vikram.m@example.com', '9876543220', '2002-10-15', 'Male', 'Computer Science', 6, '67, Vasant Vihar, New Delhi'),
('SMS-2024-012', 'Tanvi', 'Kulkarni', 'tanvi.k@example.com', '9876543221', '2003-03-29', 'Female', 'Computer Science', 4, '22, Kothrud, Pune');

-- Enrollments (Student-Course assignments)
INSERT INTO enrollments (student_id, course_id, enrollment_date, semester, academic_year, status) VALUES
(1, 1, '2024-01-10', 3, '2024-25', 'COMPLETED'),
(1, 2, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(1, 3, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(2, 2, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(2, 3, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(3, 4, '2024-07-15', 5, '2024-25', 'ACTIVE'),
(4, 1, '2024-01-10', 3, '2024-25', 'ACTIVE'),
(5, 5, '2024-01-10', 3, '2024-25', 'ACTIVE'),
(6, 2, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(6, 3, '2024-07-15', 4, '2024-25', 'ACTIVE'),
(7, 6, '2024-07-15', 6, '2024-25', 'ACTIVE'),
(8, 4, '2024-07-15', 5, '2024-25', 'ACTIVE'),
(9, 5, '2024-01-10', 3, '2024-25', 'ACTIVE'),
(10, 1, '2024-01-10', 3, '2024-25', 'ACTIVE'),
(11, 6, '2024-07-15', 6, '2024-25', 'ACTIVE'),
(12, 3, '2024-07-15', 4, '2024-25', 'ACTIVE');

-- Marks for Enrollments (Internal 20 + Mid 30 + End 50 = 100)
INSERT INTO marks (enrollment_id, internal_marks, mid_term_marks, end_term_marks, total_marks, grade, remarks) VALUES
(1, 19.00, 28.50, 48.00, 95.50, 'A+', 'Outstanding performance in DS algorithms'),
(2, 18.00, 27.00, 44.00, 89.00, 'A', 'Excellent query optimization skills'),
(3, 19.50, 29.00, 47.50, 96.00, 'A+', 'Exceptional OOP and multi-threading project work'),
(4, 17.50, 26.00, 42.50, 86.00, 'A', 'Strong analytical foundation in DBMS'),
(5, 18.50, 28.00, 46.00, 92.50, 'A+', 'Very clean modular Java architecture'),
(6, 15.00, 22.00, 38.00, 75.00, 'B', 'Good grasp of network protocols'),
(7, 19.00, 27.00, 45.00, 91.00, 'A+', 'Consistently top marks in data structures'),
(8, 14.00, 20.00, 31.00, 65.00, 'C', 'Needs more lab practice with microcontrollers'),
(9, 16.50, 25.00, 40.50, 82.00, 'B+', 'Proficient in SQL and relational design'),
(10, 17.00, 26.50, 41.50, 85.00, 'A', 'Thorough Java OOP concepts'),
(11, 20.00, 29.50, 48.50, 98.00, 'A+', 'Exceptional neural network course project'),
(12, 16.00, 23.00, 36.00, 75.00, 'B', 'Solid networking concepts'),
(13, 13.50, 19.00, 28.50, 61.00, 'C', 'Passed with foundational lab knowledge'),
(14, 18.00, 26.00, 43.00, 87.00, 'A', 'Great problem solving in algorithm analysis'),
(15, 19.00, 28.00, 47.00, 94.00, 'A+', 'Outstanding AI coursework and evaluation'),
(16, 17.00, 25.00, 41.00, 83.00, 'B+', 'Good programming design patterns');
