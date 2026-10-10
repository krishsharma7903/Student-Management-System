package com.sms.model;

/**
 * Interface contract for entities and components that can evaluate marks into academic grades.
 * 
 * // [OOP] Interface implementation
 */
public interface Gradable {
    /**
     * Calculates the letter grade corresponding to a percentage/total score (0-100).
     * 
     * @param score total score between 0 and 100
     * @return letter grade ("A+", "A", "B+", "B", "C", "F")
     */
    String calculateGrade(double score);
}
