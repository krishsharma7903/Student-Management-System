# College Project Viva Examination: 25 High-Frequency Q&A

This document contains 25 likely questions asked by academic examiners and project evaluators during viva presentations, along with concise, technically rigorous answers based directly on this codebase.

---

### Q1. How is OOP Inheritance and Polymorphism structured in your application?
**Answer:**  
We defined an abstract base class `Person` containing common human attributes (`id`, `name`, `email`, `phone`). Concrete domain entities `Student`, `Teacher`, and `Admin` extend `Person`.
- **Runtime Polymorphism:** Overriding abstract methods `getRole()`, `displayDetails()`, and interface method `calculateGrade()`. At runtime, a `Person` reference invokes the appropriate subclass implementation.
- **Compile-time Polymorphism:** Method overloading in `StudentDAO` with three variations of `searchStudent()` accepting `(int id)`, `(String name)`, and `(String courseCode, int semester)`.

---

### Q2. Why did you choose an abstract class for `Person` instead of an interface?
**Answer:**  
An abstract class is used because `Student`, `Teacher`, and `Admin` share state (`name`, `email`, `phone`) and non-static fields. Interfaces in Java cannot hold non-static instance fields. Interfaces (`Gradable`, `Reportable`) were reserved for contracts defining behavior across unrelated classes.

---

### Q3. What is the difference between method overriding and method overloading in your code?
**Answer:**  
- **Overriding (Runtime):** Subclasses (`Student`, `Teacher`) provide specific implementations of `Person.getRole()`. The JVM decides which method to invoke dynamically based on the object's runtime type.
- **Overloading (Compile-Time):** `StudentDAOImpl` declares multiple methods named `searchStudent` with different parameter signatures. The compiler determines which method to call at compile-time based on the arguments passed.

---

### Q4. How does your system handle custom exceptions, and why not just throw `SQLException`?
**Answer:**  
Throwing raw `SQLException` leaks internal database schema details, table names, and vendor errors to the user or web layer. We wrap checked SQLExceptions into domain-specific runtime exceptions (`StudentNotFoundException`, `DuplicateEmailException`, `InvalidMarksException`, `DatabaseException`). This preserves layered encapsulation and allows presentation layers to display friendly error messages without raw stack traces.

---

### Q5. What is `try-with-resources` and how does it prevent resource leaks in JDBC?
**Answer:**  
`try-with-resources` (introduced in Java 7) automatically closes any object implementing `java.lang.AutoCloseable` at the end of the statement block, even if an exception occurs. In our DAOs, `Connection`, `PreparedStatement`, and `ResultSet` are opened inside the try statement, guaranteeing database connections and cursor memory are released immediately back to the pool.

---

### Q6. How are `Comparable` and `Comparator` utilized in sorting students?
**Answer:**  
- **`Comparable<Student>`**: Implemented directly in `Student.java` (`compareTo`) to define the natural sorting order by roll number ascending.
- **`Comparator<Student>`**: Implemented as reusable constants in `CollectionsUtil` (`BY_NAME`, `BY_MARKS_DESC`, `BY_SEMESTER`) for flexible, ad-hoc multi-attribute sorting without modifying the model class.

---

### Q7. Why did you use `TreeMap` for the rank list instead of `HashMap`?
**Answer:**  
`HashMap` does not maintain any key ordering. In `ReportService.getOrderedRankList()`, we used `TreeMap<Double, List<Student>>(Collections.reverseOrder())` because `TreeMap` is backed by a Red-Black tree that automatically stores keys (student marks) in sorted descending order, enabling immediate extraction of ranked score tiers.

---

### Q8. Where and why is `LinkedHashMap` used in the reporting module?
**Answer:**  
`LinkedHashMap` maintains a doubly-linked list through its entries, preserving insertion order. It is used in `CollectionsUtil.getDepartmentWiseStudentCount()` and `CourseDAOImpl.getCourseEnrollmentCounts()` so that sorted SQL aggregation outputs retain their exact ordering when rendered on UI bar charts.

---

### Q9. How do you utilize Java 17 Streams and lambdas in the project?
**Answer:**  
Streams process collections declaratively:
- **Filtering & Limiting:** In `ReportService.getOverallToppers()`, students are filtered (`s.getAverageMarks() > 0`), sorted descending by marks using lambda comparator `(s1, s2) -> Double.compare(s2.getAverageMarks(), s1.getAverageMarks())`, and truncated with `.limit(10)`.
- **Grouping:** `Collectors.groupingBy()` groups students by department in `CollectionsUtil`.

---

### Q10. What is the generic class `ResultPage<T>` and what benefits does it offer?
**Answer:**  
`ResultPage<T>` is a generic container with type parameter `T`. It encapsulates pagination metadata (page number, page size, total pages, total record count) alongside a `List<T>` of data. This allows `StudentDAOImpl`, `CourseDAOImpl`, `EnrollmentDAOImpl`, and `MarksDAOImpl` to share one type-safe pagination abstraction without duplicating pagination logic.

---

### Q11. Explain your generic method `<T extends Person> void printAll(List<T> list)`.
**Answer:**  
In `CollectionsUtil`, `<T extends Person>` uses a bounded type parameter. It ensures the method accepts only collections whose elements are `Person` or subclasses (`Student`, `Teacher`, `Admin`). This guarantees type safety while allowing the method to invoke polymorphic methods like `displayDetails()` without explicit type casting.

---

### Q12. Why do you use an `ExecutorService` thread pool instead of creating `new Thread()`?
**Answer:**  
Spawning a `new Thread()` per request creates high overhead and risks JVM memory exhaustion under load. `ThreadPoolManager` uses `Executors.newFixedThreadPool(4)` to reuse worker daemon threads, queue tasks cleanly, and run heavy operations (asynchronous email notifications and report calculations) without blocking the Tomcat HTTP request thread.

---

### Q13. What exact concurrency race condition does the `ReentrantLock` in `StudentService` prevent?
**Answer:**  
When two administrators concurrently register students, both threads might query the database count at the same millisecond, read the same value (e.g. 12), and calculate the identical roll number `SMS-2026-013`. The second insert would fail with a SQL unique constraint violation. The fair `ReentrantLock` ensures only one thread enters the sequence generation critical section at a time.

---

### Q14. Why is `AtomicInteger` used for active user tracking instead of a regular `int`?
**Answer:**  
Multiple client sessions can be created or destroyed simultaneously across concurrent worker threads. A regular `int` increment (`count++`) is not atomic (it involves read-modify-write). `AtomicInteger` uses low-level hardware Compare-And-Swap (CAS) instructions to guarantee thread-safe incrementing and decrementing without locking.

---

### Q15. What are the roles of `ServletContextListener` and `HttpSessionListener`?
**Answer:**  
- **`AppContextListener` (`ServletContextListener`):** Listens to application startup and shutdown. Initializes the thread pool and verifies the default admin user on startup; terminates worker threads and shuts down MySQL cleanup threads on shutdown.
- **`SessionListener` (`HttpSessionListener`):** Listens to session creation and expiration, updating the active user count in application scope.

---

### Q16. How does your `DBConnection` ensure thread safety without synchronization overhead?
**Answer:**  
`DBConnection` utilizes the **Double-Checked Locking Singleton** pattern with a `volatile` instance field. The `synchronized` block is executed only on the first access when `instance == null`. Subsequent calls return the cached singleton immediately without synchronization overhead.

---

### Q17. Why must `PreparedStatement` be used instead of `Statement`?
**Answer:**  
1. **Security:** `PreparedStatement` uses parameter placeholders (`?`) where user inputs are treated strictly as literal values, completely neutralizing SQL Injection attacks.
2. **Performance:** The database compiles and optimizes the SQL execution plan once; subsequent executions reuse the precompiled query with new parameters.

---

### Q18. How do you implement ACID transactions in plain JDBC?
**Answer:**  
In `EnrollmentDAOImpl.enrollWithInitialMarksTransaction()` and `MarksDAOImpl.updateMarksTransaction()`:
1. Turn off autocommit: `conn.setAutoCommit(false);`
2. Execute statement 1 (insert enrollment) and retrieve generated key.
3. Execute statement 2 (insert initial marks record).
4. Commit both atomically: `conn.commit();`
5. Inside the `catch (SQLException e)` block, invoke `conn.rollback();` so partial data is never committed.

---

### Q19. How does batch insertion work in `StudentDAOImpl.batchInsert()`?
**Answer:**  
Instead of executing an individual network round-trip per row, `ps.addBatch()` accumulates multiple SQL inserts in memory. When complete, `ps.executeBatch()` sends all rows to the MySQL server in a single TCP payload, improving bulk import speed.

---

### Q20. Explain the SQL subquery used in `getTopperPerCourse()`.
**Answer:**  
```sql
SELECT c.course_code, s.roll_number, m.total_marks
FROM marks m
INNER JOIN enrollments e ON m.enrollment_id = e.id
INNER JOIN students s ON e.student_id = s.id
INNER JOIN courses c ON e.course_id = c.id
WHERE m.total_marks = (
    SELECT MAX(m2.total_marks)
    FROM marks m2
    INNER JOIN enrollments e2 ON m2.enrollment_id = e2.id
    WHERE e2.course_id = e.course_id
)
```
This is a **correlated subquery**. For each outer enrollment, the inner subquery calculates the maximum score achieved specifically in that course (`e2.course_id = e.course_id`). The outer query filters for students whose score equals that maximum.

---

### Q21. What is a Database VIEW and why did you create `v_student_results`?
**Answer:**  
A database VIEW is a saved virtual table based on the result of a SQL query. We created `v_student_results` to encapsulate complex four-table INNER and LEFT JOINs between `students`, `courses`, `enrollments`, and `marks`. It simplifies DAO queries (`SELECT * FROM v_student_results`), improves read performance, and centralizes report query definitions.

---

### Q22. How does `AuthFilter` protect secure URLs and enforce RBAC?
**Answer:**  
`AuthFilter` intercepts every incoming HTTP request (`@WebFilter("/*")`):
1. Bypasses public endpoints (`/login`, `/logout`, `/css/*`, `/js/*`).
2. Checks `session.getAttribute("user")`. If absent, redirects unauthenticated requests to `/login`.
3. Enforces RBAC: If a user with role `TEACHER` attempts an admin-only destructive action (e.g., `/students?action=delete` or `/courses?action=add`), it blocks access, sets a flash error, and redirects to `/dashboard`.

---

### Q23. What is the Post-Redirect-Get (PRG) pattern and where is it used?
**Answer:**  
When a user submits a form via POST (e.g., student addition), the servlet processes the data, sets a success message in session, and sends an HTTP 302 redirect (`response.sendRedirect()`) to a GET endpoint (`/students`). This prevents the duplicate form submission dialog when the user refreshes the page or clicks Back.

---

### Q24. How does the "Remember Me" feature work using Cookies?
**Answer:**  
In `LoginServlet`, when the user checks "Remember username", the servlet creates a persistent cookie `Cookie("sms_remembered_user", username)` with `setMaxAge(7 * 24 * 60 * 60)` (7 days) and adds it to the HTTP response. On future visits, `LoginServlet.doGet()` reads this cookie and pre-fills the username field.

---

### Q25. What is the difference between `RequestDispatcher.forward()` and `HttpServletResponse.sendRedirect()`?
**Answer:**  
- **`forward()` (Server-Side):** The servlet transfers request control internally to a JSP view within the same container. The browser URL does not change, and request-scoped attributes (`request.setAttribute()`) are preserved.
- **`sendRedirect()` (Client-Side):** The server returns an HTTP 302 redirect response instructing the browser to issue a new HTTP GET request to the target URL. The browser address bar changes, and request attributes are discarded (session attributes must be used).
