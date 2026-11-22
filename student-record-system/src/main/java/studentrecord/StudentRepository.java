package studentrecord;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** Data access layer: all SQL lives here (parameterized queries only). */
@Repository
public class StudentRepository {

    private static final RowMapper<Student> MAPPER = (rs, i) -> new Student(
            rs.getString("prn"),
            rs.getString("name"),
            rs.getString("course"),
            rs.getInt("year"),
            rs.getString("phone"));

    private final JdbcTemplate jdbc;

    public StudentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Student> findAll(String q) {
        if (q == null || q.isBlank()) {
            return jdbc.query("SELECT * FROM students ORDER BY name", MAPPER);
        }
        String like = "%" + q.trim().toLowerCase() + "%";
        return jdbc.query(
                "SELECT * FROM students WHERE LOWER(prn) LIKE ? OR LOWER(name) LIKE ? ORDER BY name",
                MAPPER, like, like);
    }

    public Optional<Student> findByPrn(String prn) {
        return jdbc.query("SELECT * FROM students WHERE prn = ?", MAPPER, prn)
                .stream().findFirst();
    }

    public void insert(Student s) {
        jdbc.update(
                "INSERT INTO students (prn, name, course, year, phone) VALUES (?, ?, ?, ?, ?)",
                s.prn(), s.name(), s.course(), s.year(), s.phone());
    }

    /** Returns number of rows updated (0 = PRN not found). */
    public int update(Student s) {
        return jdbc.update(
                "UPDATE students SET name = ?, course = ?, year = ?, phone = ? WHERE prn = ?",
                s.name(), s.course(), s.year(), s.phone(), s.prn());
    }

    /** Returns number of rows deleted (0 = PRN not found). */
    public int delete(String prn) {
        return jdbc.update("DELETE FROM students WHERE prn = ?", prn);
    }
}
