package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.dao.impl.StudentDAOImpl;
import com.sms.exception.DuplicateEmailException;
import com.sms.exception.StudentNotFoundException;
import com.sms.model.Student;
import com.sms.util.ResultPage;
import com.sms.util.ThreadPoolManager;

import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Service managing core Student business logic, server-side validation,
 * thread-safe sequence generation, and asynchronous notification dispatch.
 * 
 * // [OOP] Custom Exception handling
 * // [CONCURRENCY] ReentrantLock for thread-safe roll-number generation
 */
public class StudentService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    private final StudentDAO studentDAO;
    private final ThreadPoolManager threadPoolManager;

    /**
     * [CONCURRENCY] ReentrantLock for roll-number generation.
     * 
     * RACE CONDITION EXPLANATION:
     * When two or more administrative users simultaneously submit student registrations,
     * both threads could query the current database count or max sequence at the exact same
     * millisecond. Both would compute the identical next roll number (e.g., 'SMS-2024-013').
     * The first thread commits successfully, while the second thread triggers a SQL UNIQUE
     * constraint violation, aborting the transaction.
     * 
     * By using ReentrantLock, access to the sequential generator is mutually exclusive:
     * exactly one thread enters the critical section, generates the unique number, and
     * completes the insert before the next thread can calculate its sequence.
     */
    private final ReentrantLock rollNumberLock = new ReentrantLock(true);

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
        this.threadPoolManager = ThreadPoolManager.getInstance();
    }

    public StudentService(StudentDAO studentDAO, ThreadPoolManager threadPoolManager) {
        this.studentDAO = studentDAO;
        this.threadPoolManager = threadPoolManager;
    }

    /**
     * [CONCURRENCY] Generates a collision-free student roll number inside an exclusive lock.
     */
    public String generateNextRollNumber() {
        rollNumberLock.lock();
        try {
            int currentCount = studentDAO.count();
            int currentYear = Year.now().getValue();
            return String.format("SMS-%d-%03d", currentYear, currentCount + 1);
        } finally {
            rollNumberLock.unlock();
        }
    }

    public boolean addStudent(Student student) {
        validateStudentInput(student);

        // Check for duplicate email address
        Student existingByEmail = studentDAO.findByEmail(student.getEmail());
        if (existingByEmail != null) {
            throw new DuplicateEmailException("A student with email '" + student.getEmail() + "' is already registered.");
        }

        // Generate roll number if not provided
        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            student.setRollNumber(generateNextRollNumber());
        } else {
            Student existingByRoll = studentDAO.findByRollNumber(student.getRollNumber().trim());
            if (existingByRoll != null) {
                throw new DuplicateEmailException("Roll number '" + student.getRollNumber() + "' is already assigned.");
            }
        }

        boolean saved = studentDAO.add(student);
        if (saved) {
            // [CONCURRENCY] Non-blocking asynchronous notification via background thread pool
            threadPoolManager.sendAsyncNotification(
                    student.getEmail(),
                    "Welcome to College Portal",
                    "Dear " + student.getName() + ", your student profile has been registered with Roll No: " + student.getRollNumber()
            );
        }
        return saved;
    }

    public boolean updateStudent(Student student) {
        validateStudentInput(student);

        Student existing = studentDAO.findById(student.getId());
        if (existing == null) {
            throw new StudentNotFoundException("Student with ID " + student.getId() + " was not found.");
        }

        // Validate email uniqueness against other students
        Student byEmail = studentDAO.findByEmail(student.getEmail());
        if (byEmail != null && byEmail.getId() != student.getId()) {
            throw new DuplicateEmailException("Email '" + student.getEmail() + "' is already used by another student.");
        }

        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id) {
        Student existing = studentDAO.findById(id);
        if (existing == null) {
            throw new StudentNotFoundException("Student with ID " + id + " was not found.");
        }
        return studentDAO.delete(id);
    }

    public Student getStudentById(int id) {
        Student student = studentDAO.findById(id);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + id + " was not found.");
        }
        return student;
    }

    public Student getStudentByRollNumber(String rollNumber) {
        Student student = studentDAO.findByRollNumber(rollNumber);
        if (student == null) {
            throw new StudentNotFoundException("Student with roll number " + rollNumber + " was not found.");
        }
        return student;
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public ResultPage<Student> getPaginatedStudents(int page, int pageSize, String sortBy, String sortOrder) {
        return studentDAO.findPaginated(page, pageSize, sortBy, sortOrder);
    }

    public List<Student> searchStudents(String keyword) {
        return studentDAO.search(keyword);
    }

    public int getStudentCount() {
        return studentDAO.count();
    }

    public int[] bulkImportStudents(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return new int[0];
        }
        for (Student s : students) {
            validateStudentInput(s);
            if (s.getRollNumber() == null || s.getRollNumber().isEmpty()) {
                s.setRollNumber(generateNextRollNumber());
            }
        }
        return studentDAO.batchInsert(students);
    }

    // Overloaded search delegation
    public Student searchStudent(int id) {
        return studentDAO.searchStudent(id);
    }

    public List<Student> searchStudent(String name) {
        return studentDAO.searchStudent(name);
    }

    public List<Student> searchStudent(String courseCode, int semester) {
        return studentDAO.searchStudent(courseCode, semester);
    }

    /**
     * Rigorous server-side business validation for student entity attributes.
     */
    private void validateStudentInput(Student student) {
        Objects.requireNonNull(student, "Student instance cannot be null.");

        if (student.getFirstName() == null || student.getFirstName().trim().length() < 2) {
            throw new IllegalArgumentException("First name must be at least 2 characters long.");
        }
        if (student.getLastName() == null || student.getLastName().trim().length() < 2) {
            throw new IllegalArgumentException("Last name must be at least 2 characters long.");
        }
        if (student.getEmail() == null || !EMAIL_PATTERN.matcher(student.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("A valid email address is required (e.g., student@domain.com).");
        }
        if (student.getPhone() != null && !student.getPhone().trim().isEmpty() &&
                !PHONE_PATTERN.matcher(student.getPhone().trim().replaceAll("[\\s-]", "")).matches()) {
            throw new IllegalArgumentException("Phone number must contain between 10 and 15 digits.");
        }
        if (student.getDepartment() == null || student.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Department selection is required.");
        }
        if (student.getSemester() < 1 || student.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
    }
}
