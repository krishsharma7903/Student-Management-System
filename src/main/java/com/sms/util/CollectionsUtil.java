package com.sms.util;

import com.sms.model.Course;
import com.sms.model.Person;
import com.sms.model.Student;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility class demonstrating Java Collections Framework, Generics, Comparators, and Streams.
 * 
 * // [COLLECTIONS] Collections & Generics demonstrations
 */
public final class CollectionsUtil {

    private static final Logger LOGGER = Logger.getLogger(CollectionsUtil.class.getName());

    private CollectionsUtil() {
        // Utility constructor
    }

    // [GENERICS] Generic method with bounded type parameter <T extends Person>
    public static <T extends Person> void printAll(List<T> list) {
        if (list == null || list.isEmpty()) {
            LOGGER.info("List is empty or null.");
            return;
        }
        for (T item : list) {
            LOGGER.info(() -> "[Person Hierarchy] " + item.displayDetails());
        }
    }

    // [COLLECTIONS] Comparators for flexible multi-attribute sorting
    public static final Comparator<Student> BY_NAME = Comparator.comparing(
            Student::getName, 
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
    );

    public static final Comparator<Student> BY_ROLL_NUMBER = Comparator.comparing(
            Student::getRollNumber, 
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
    );

    public static final Comparator<Student> BY_MARKS_DESC = (s1, s2) -> 
            Double.compare(s2.getAverageMarks(), s1.getAverageMarks());

    public static final Comparator<Student> BY_SEMESTER = Comparator.comparingInt(Student::getSemester);

    /**
     * [COLLECTIONS] Uses Set<String> to enforce and verify unique student emails.
     */
    public static Set<String> extractUniqueEmails(List<Student> students) {
        if (students == null) return Collections.emptySet();
        return students.stream()
                .map(Student::getEmail)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * [COLLECTIONS] Uses TreeMap for naturally ordered rank list (Marks -> List of Students).
     */
    public static TreeMap<Double, List<Student>> buildRankList(List<Student> students) {
        // Reverse order so highest marks appear first
        TreeMap<Double, List<Student>> rankMap = new TreeMap<>(Collections.reverseOrder());
        if (students == null) return rankMap;

        for (Student student : students) {
            rankMap.computeIfAbsent(student.getAverageMarks(), k -> new ArrayList<>()).add(student);
        }
        return rankMap;
    }

    /**
     * [COLLECTIONS] Uses LinkedHashMap to preserve insertion order for reports.
     */
    public static LinkedHashMap<String, Long> getDepartmentWiseStudentCount(List<Student> students) {
        if (students == null) return new LinkedHashMap<>();
        return students.stream()
                .filter(s -> s.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        Student::getDepartment,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * [COLLECTIONS] Streams & Lambdas: Top N topper students across the institution.
     */
    public static List<Student> getTopStudents(List<Student> students, int limit) {
        if (students == null) return Collections.emptyList();
        return students.stream()
                .sorted(BY_MARKS_DESC)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * [COLLECTIONS] Streams groupingBy: Groups students by their department.
     */
    public static Map<String, List<Student>> groupStudentsByDepartment(List<Student> students) {
        if (students == null) return Collections.emptyMap();
        return students.stream()
                .collect(Collectors.groupingBy(Student::getDepartment));
    }

    /**
     * [COLLECTIONS] Map<Integer, List<Course>> mapping student IDs to their enrolled courses.
     */
    public static Map<Integer, List<Course>> createStudentCourseMap() {
        return new HashMap<>();
    }
}
