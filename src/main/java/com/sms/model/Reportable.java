package com.sms.model;

import java.util.Map;

/**
 * Interface contract for domain entities capable of emitting structured metadata
 * for analytics and report generation.
 * 
 * // [OOP] Interface implementation
 */
public interface Reportable {
    /**
     * Generates a key-value data map representing the entity's reporting metrics.
     * 
     * @return Map containing formatted reporting attributes
     */
    Map<String, Object> generateReportData();
}
