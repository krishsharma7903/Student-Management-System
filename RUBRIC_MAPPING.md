# Rubric Compliance & Code Mapping Guide

This document maps every single requirement and evaluation rubric item (from Rubrics 1 & 2) directly to the exact source files and implementations within the project.

---

## RUBRIC 1: CORE JAVA & DATABASE

### 1.1 Object-Oriented Programming (OOP) Implementation (10 Marks)

| Requirement | Code Implementation & Location | Description & Code Pattern |
| :--- | :--- | :--- |
| **Inheritance** | [`Person.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Person.java)<br>[`Student.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Student.java)<br>[`Teacher.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Teacher.java)<br>[`Admin.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Admin.java) | Abstract base class `Person` defines shared state (`id`, `name`, `email`, `phone`). Extended by subclasses `Student`, `Teacher`, and `Admin`. |
| **Runtime Polymorphism** (Method Overriding) | [`Student.java#L45-L68`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Student.java#L45-L68)<br>[`Teacher.java#L30-L40`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Teacher.java#L30-L40)<br>[`Admin.java#L28-L38`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Admin.java#L28-L38) | Concrete overrides of abstract methods `getRole()`, `displayDetails()`, and interface method `calculateGrade(double score)`. Dynamic method dispatch occurs at runtime. |
| **Polymorphic Reference Demo** | [`JdbcDemo.java#L32-L55`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/JdbcDemo.java#L32-L55) | `List<Person>` holds `Student`, `Teacher`, and `Admin` objects; loops polymorphically invoking `person.getRole()` and `person.displayDetails()`. |
| **Compile-Time Polymorphism** (Method Overloading) | [`StudentDAO.java#L24-L27`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/StudentDAO.java#L24-L27)<br>[`StudentDAOImpl.java#L265-L317`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/StudentDAOImpl.java#L265-L317) | Three distinct overloaded methods:<br>1. `Student searchStudent(int id)`<br>2. `List<Student> searchStudent(String name)`<br>3. `List<Student> searchStudent(String courseCode, int semester)` |
| **Interfaces Contract** | [`StudentDAO.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/StudentDAO.java)<br>[`CourseDAO.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/CourseDAO.java)<br>[`EnrollmentDAO.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/EnrollmentDAO.java)<br>[`MarksDAO.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/MarksDAO.java)<br>[`UserDAO.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/UserDAO.java)<br>[`Gradable.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Gradable.java)<br>[`Reportable.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Reportable.java) | Defined contracts implemented by respective DAO implementations (`*DAOImpl`) and models (`Student`, `Marks`, `Course`). |
| **Encapsulation** | All classes in [`com.sms.model`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/) | All instance variables are declared `private` with explicit getters, setters, parameterized constructors, `equals()`, `hashCode()`, and `toString()`. |
| **Custom Exceptions** | [`StudentNotFoundException.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/exception/StudentNotFoundException.java)<br>[`DuplicateEmailException.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/exception/DuplicateEmailException.java)<br>[`InvalidMarksException.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/exception/InvalidMarksException.java)<br>[`AuthenticationException.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/exception/AuthenticationException.java)<br>[`DatabaseException.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/exception/DatabaseException.java) | Checked `SQLException`s are safely intercepted and rethrown as unchecked `DatabaseException` with descriptive messages. Prevents leaking database stack traces to clients. |

---

### 1.2 Collections, Generics & Streams (6 Marks)

| Requirement | Code Implementation & Location | Description & Code Pattern |
| :--- | :--- | :--- |
| **Generic Class `ResultPage<T>`** | [`ResultPage.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/ResultPage.java) | Generic class `ResultPage<T>` encapsulates data items `List<T>`, current page, page size, total pages, and total count. |
| **Generic Method `<T extends Person>`** | [`CollectionsUtil.java#L26-L34`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/CollectionsUtil.java#L26-L34) | `<T extends Person> void printAll(List<T> list)` accepts any subclass of `Person` and invokes polymorphic methods. |
| **Collections Framework Data Structures** | [`CollectionsUtil.java#L52-L95`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/CollectionsUtil.java#L52-L95)<br>[`ReportService.java#L80-L115`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/ReportService.java#L80-L115) | - `List<Student>`: entity stores and transfer objects.<br>- `Set<String>`: `extractUniqueEmails()` verifying unique emails.<br>- `TreeMap<Double, List<Student>>`: `buildRankList()` keeping descending score order.<br>- `LinkedHashMap<String, Long>`: preserving department order.<br>- `Map<Integer, List<Course>>`: mapping student ID to courses. |
| **`Comparable<T>` & `Comparator<T>`** | [`Student.java#L78-L86`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/model/Student.java#L78-L86)<br>[`CollectionsUtil.java#L36-L50`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/CollectionsUtil.java#L36-L50) | - `Student` implements `Comparable<Student>` for natural ordering by roll number.<br>- `CollectionsUtil.BY_NAME`, `BY_ROLL_NUMBER`, `BY_MARKS_DESC`, `BY_SEMESTER` comparators. |
| **Streams & Lambdas & Grouping** | [`CollectionsUtil.java#L69-L95`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/CollectionsUtil.java#L69-L95)<br>[`ReportService.java#L68-L77`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/ReportService.java#L68-L77) | Uses Java 17 Streams with `.filter()`, `.sorted()`, `.limit()`, and `Collectors.groupingBy()` for department metrics and topper rankings. |

---

### 1.3 Multithreading & Synchronization (4 Marks)

| Requirement | Code Implementation & Location | Description & Code Pattern |
| :--- | :--- | :--- |
| **`ExecutorService` Thread Pool** | [`ThreadPoolManager.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/ThreadPoolManager.java) | Configures a fixed pool of daemon worker threads for async non-blocking tasks. |
| **Asynchronous Notifications** | [`ThreadPoolManager.java#L58-L73`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/ThreadPoolManager.java#L58-L73)<br>[`StudentService.java#L80-L86`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/StudentService.java#L80-L86) | Non-blocking simulated email delivery dispatched to a worker thread upon student registration. |
| **`Callable` & `Future` Pattern** | [`ReportService.java#L47-L65`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/ReportService.java#L47-L65)<br>[`DashboardServlet.java#L35-L48`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/DashboardServlet.java#L35-L48) | `Callable<Map<String, Object>>` computes dashboard metrics asynchronously; `Future.get()` resolves metrics in `DashboardServlet`. |
| **Thread-Safe Roll Number Generator (`ReentrantLock`)** | [`StudentService.java#L33-L64`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/StudentService.java#L33-L64) | Uses fair `ReentrantLock` around sequential roll number calculation. Prevents concurrency race conditions where concurrent threads read identical counts and cause unique constraint collisions. |
| **`AtomicInteger` Active Users Counter** | [`SessionListener.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/listener/SessionListener.java) | Tracks live concurrent user sessions atomically using `incrementAndGet()` and `decrementAndGet()`. |
| **Lifecycle Listener (`ServletContextListener`)** | [`AppContextListener.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/listener/AppContextListener.java) | Initializes thread pools, sets up application scope attributes on startup, and safely shuts down thread pools and MySQL cleanup threads on shutdown. |

---

### 1.4 Database Operations & Service Layer (7 Marks)

| Requirement | Code Implementation & Location | Description & Code Pattern |
| :--- | :--- | :--- |
| **`DBConnection` Singleton** | [`DBConnection.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/DBConnection.java) | Thread-safe Singleton loading credentials from `db.properties` with fallback classpath resolution. Zero hardcoded credentials. |
| **Separate DAO Interface & Impl** | [`com.sms.dao`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/) & [`com.sms.dao.impl`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/) | Dedicated interfaces and JDBC implementations for `StudentDAO`, `CourseDAO`, `EnrollmentDAO`, `MarksDAO`, and `UserDAO`. |
| **Full CRUD & Methods** | All classes in [`com.sms.dao.impl`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/) | `add`, `update`, `delete`, `findById`, `findAll`, `search`, `count`, pagination (`LIMIT ? OFFSET ?`), dynamic sorting. |
| **Thin Servlets & Service Layer** | [`com.sms.service`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/) | Servlets delegate directly to `StudentService`, `CourseService`, `EnrollmentService`, `MarksService`, `AuthService`, and `ReportService`. |

---

### 1.5 & 1.6 JDBC Connectivity & SQL Requirements (3 + 3 Marks)

| Requirement | Code Implementation & Location | Description & Code Pattern |
| :--- | :--- | :--- |
| **`PreparedStatement` Only** | All DAO classes in [`com.sms.dao.impl`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/) | 100% parametrized SQL queries preventing SQL injection vulnerabilities. Explicit ResultSet-to-object mapping. |
| **ACID Transactions (`commit`/`rollback`)** | [`EnrollmentDAOImpl.java#L66-L131`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/EnrollmentDAOImpl.java#L66-L131)<br>[`MarksDAOImpl.java#L67-L127`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/MarksDAOImpl.java#L67-L127) | `conn.setAutoCommit(false)`, multi-statement execution, `conn.commit()`, and `conn.rollback()` inside catch blocks. |
| **Batch Insert for Bulk Import** | [`StudentDAOImpl.java#L248-L263`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/StudentDAOImpl.java#L248-L263) | `ps.addBatch()` and `ps.executeBatch()` within an atomic transaction. |
| **Standalone Console `JdbcDemo`** | [`JdbcDemo.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/util/JdbcDemo.java) | Connects, inserts record, queries, updates, and deletes test record. Run with `java -cp target/classes;... com.sms.util.JdbcDemo`. |
| **Database Schema & Sample Data** | [`schema.sql`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/schema.sql) | DDL scripts creating `users`, `students`, `courses`, `enrollments`, `marks` tables with PK, FK `ON DELETE CASCADE`, `CHECK (total_marks BETWEEN 0 AND 100)`, indexes, and 10+ students, 6 courses, enrollments, marks. |
| **Correlated Subquery** | [`MarksDAOImpl.java#L268-L298`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/MarksDAOImpl.java#L268-L298) | Subquery selecting top student in each course: `WHERE m.total_marks = (SELECT MAX(m2.total_marks) FROM marks m2 ... WHERE e2.course_id = e.course_id)`. |
| **Database VIEW** | [`schema.sql#L90-L113`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/schema.sql#L90-L113)<br>[`MarksDAOImpl.java#L300-L330`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/dao/impl/MarksDAOImpl.java#L300-L330) | `CREATE VIEW v_student_results AS ...` queried via `MarksDAO.getResultViewReports()`. |
| **BCrypt Password Hashing** | [`AuthService.java#L30-L59`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/service/AuthService.java#L30-L59) | Passwords stored with salted BCrypt hashes. Dynamically provisions `admin` / `admin123` on startup. |

---

## RUBRIC 2: WEB-BASED PROJECT

### 2.1 Solution Design & Documentation (8 Marks)
- [`README.md`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/README.md): Comprehensive system architecture, Mermaid ER diagram, Mermaid Class diagram, use cases, folder tree, and deployment guide.
- [`RUBRIC_MAPPING.md`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/RUBRIC_MAPPING.md): Exact rubric verification matrix.
- [`VIVA_QA.md`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/VIVA_QA.md): 25 technical examination viva questions and answers.

### 2.2 Core Java Concepts Visible in Code (10 Marks)
All Java concepts are annotated in source code comments:
- `// [OOP] Inheritance: abstract class Person`
- `// [OOP] Polymorphism: runtime method overriding`
- `// [OOP] Polymorphism: compile-time overloading`
- `// [CONCURRENCY] ReentrantLock for roll-number generation`
- `// [CONCURRENCY] ExecutorService thread pool`
- `// [COLLECTIONS] Streams groupingBy`
- `// [COLLECTIONS] TreeMap rank list`
- `// [JDBC] PreparedStatement only`
- `// [JDBC] Transactions commit/rollback`

### 2.3 Database Integration (8 Marks)
- Complete CRUD across all domain models.
- Many-to-Many course enrollments bridge table with cascade deletion.
- Assessment score aggregation, GPA calculations, and grade distributions.

### 2.4 Servlets & Web Integration (7 Marks)
- **Controllers**: [`LoginServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/LoginServlet.java), [`LogoutServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/LogoutServlet.java), [`DashboardServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/DashboardServlet.java), [`StudentServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/StudentServlet.java), [`CourseServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/CourseServlet.java), [`EnrollmentServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/EnrollmentServlet.java), [`MarksServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/MarksServlet.java), [`ReportServlet`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/servlet/ReportServlet.java).
- **Architecture**: `doGet` / `doPost`, Post-Redirect-Get (PRG) pattern, `RequestDispatcher` forwarding, `HttpSession` state, Cookies for remember-me.
- **Security & RBAC**: [`AuthFilter.java`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/java/com/sms/filter/AuthFilter.java) guarding secure URLs and checking `ADMIN` vs `TEACHER` permissions.
- **Error Handling**: Custom styled [`error-404.jsp`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/webapp/WEB-INF/views/error-404.jsp) and [`error-500.jsp`](file:///c:/Users/krish/OneDrive/Desktop/Student%20Management%20System%20Java%20Project/src/main/webapp/WEB-INF/views/error-500.jsp) configured via `web.xml`.
- **Pure JSTL/EL**: Zero JSP scriptlets (`<% ... %>`) used in presentation files.
