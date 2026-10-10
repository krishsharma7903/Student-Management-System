package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.exception.DuplicateEmailException;
import com.sms.model.Student;
import com.sms.util.CollectionsUtil;
import com.sms.util.ResultPage;
import com.sms.util.ThreadPoolManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentService using JUnit 5.
 * 
 * // [TESTING] JUnit 5 tests for service layer & validation
 */
class StudentServiceTest {

    private StudentService studentService;
    private MockStudentDAO mockStudentDAO;

    @BeforeEach
    void setUp() {
        mockStudentDAO = new MockStudentDAO();
        studentService = new StudentService(mockStudentDAO, ThreadPoolManager.getInstance());
    }

    @Test
    @DisplayName("Should generate roll number with correct institutional prefix and sequence")
    void testRollNumberGeneration() {
        String roll = studentService.generateNextRollNumber();
        assertNotNull(roll);
        assertTrue(roll.startsWith("SMS-"), "Roll number must start with 'SMS-' prefix");
    }

    @Test
    @DisplayName("Should validate and successfully register a valid student")
    void testAddStudentSuccess() {
        Student s = new Student(1, "SMS-2024-099", "Rahul", "Dravid", "rahul@cricket.edu", 
                                "9876543210", LocalDate.of(2003, 1, 11), "Male", 
                                "Computer Science", 3, "Bangalore");

        boolean result = studentService.addStudent(s);
        assertTrue(result);
        assertEquals(1, mockStudentDAO.count());
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when registering student with existing email")
    void testAddStudentDuplicateEmail() {
        Student s1 = new Student(1, "SMS-2024-101", "Priya", "Nair", "priya@domain.com", 
                                 "9876543210", LocalDate.of(2003, 5, 20), "Female", 
                                 "Computer Science", 2, "Kochi");
        studentService.addStudent(s1);

        Student s2 = new Student(2, "SMS-2024-102", "Ankit", "Kumar", "priya@domain.com", 
                                 "9876543211", LocalDate.of(2003, 7, 14), "Male", 
                                 "Electronics", 2, "Delhi");

        assertThrows(DuplicateEmailException.class, () -> {
            studentService.addStudent(s2);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email syntax is invalid")
    void testValidateStudentInvalidEmail() {
        Student s = new Student(1, "", "Amit", "Sharma", "invalid-email-address", 
                                "9876543210", LocalDate.of(2003, 3, 15), "Male", 
                                "Computer Science", 1, "Mumbai");

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(s);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when semester is out of 1-8 bounds")
    void testValidateStudentInvalidSemester() {
        Student s = new Student(1, "", "Amit", "Sharma", "amit@valid.com", 
                                "9876543210", LocalDate.of(2003, 3, 15), "Male", 
                                "Computer Science", 12, "Mumbai");

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(s);
        });
    }

    @Test
    @DisplayName("Should correctly sort students using Comparable natural ordering and Comparators")
    void testSortingAndComparators() {
        Student s1 = new Student(1, "SMS-2024-005", "Zoya", "Akhtar", "zoya@film.com", "9876543210", null, "Female", "CS", 1, "");
        Student s2 = new Student(2, "SMS-2024-001", "Aakash", "Verma", "aakash@tech.com", "9876543211", null, "Male", "CS", 1, "");

        s1.setAverageMarks(75.0);
        s2.setAverageMarks(95.0);

        List<Student> list = new ArrayList<>(List.of(s1, s2));

        // 1. Natural ordering via Comparable (by roll number)
        Collections.sort(list);
        assertEquals("SMS-2024-001", list.get(0).getRollNumber());

        // 2. Custom Comparator sorting by marks descending
        list.sort(CollectionsUtil.BY_MARKS_DESC);
        assertEquals(95.0, list.get(0).getAverageMarks());
    }

    /**
     * In-memory mock DAO for StudentService testing.
     */
    private static class MockStudentDAO implements StudentDAO {
        private final List<Student> students = new ArrayList<>();

        @Override
        public boolean add(Student student) {
            students.add(student);
            return true;
        }

        @Override
        public boolean update(Student student) {
            return true;
        }

        @Override
        public boolean delete(int id) {
            return students.removeIf(s -> s.getId() == id);
        }

        @Override
        public Student findById(int id) {
            return students.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }

        @Override
        public Student findByRollNumber(String rollNumber) {
            return students.stream().filter(s -> s.getRollNumber().equalsIgnoreCase(rollNumber)).findFirst().orElse(null);
        }

        @Override
        public Student findByEmail(String email) {
            return students.stream().filter(s -> s.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
        }

        @Override
        public List<Student> findAll() {
            return new ArrayList<>(students);
        }

        @Override
        public ResultPage<Student> findPaginated(int page, int pageSize, String sortBy, String sortOrder) {
            return new ResultPage<>(students, page, pageSize, students.size());
        }

        @Override
        public List<Student> search(String keyword) {
            return new ArrayList<>(students);
        }

        @Override
        public int count() {
            return students.size();
        }

        @Override
        public int[] batchInsert(List<Student> list) {
            students.addAll(list);
            return new int[list.size()];
        }

        @Override
        public Student searchStudent(int id) {
            return findById(id);
        }

        @Override
        public List<Student> searchStudent(String name) {
            return new ArrayList<>(students);
        }

        @Override
        public List<Student> searchStudent(String courseCode, int semester) {
            return new ArrayList<>(students);
        }
    }
}
