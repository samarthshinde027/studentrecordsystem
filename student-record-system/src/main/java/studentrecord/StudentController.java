package studentrecord;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

/** REST API: /api/students */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repo;

    public StudentController(StudentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Student> list(@RequestParam(required = false) String q) {
        return repo.findAll(q);
    }

    @GetMapping("/{prn}")
    public Student get(@PathVariable String prn) {
        return repo.findByPrn(prn).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody Student body) {
        Student s = body.trimmed();
        if (s.prn().isEmpty() || s.name().isEmpty() || s.course().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PRN, name and course are required");
        }
        if (repo.findByPrn(s.prn()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate PRN");
        }
        try {
            repo.insert(s);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate PRN");
        }
        return s;
    }

    @PutMapping("/{prn}")
    public Student update(@PathVariable String prn, @Valid @RequestBody Student body) {
        Student t = body.trimmed();
        Student s = new Student(prn, t.name(), t.course(), t.year(), t.phone());
        if (s.name().isEmpty() || s.course().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and course are required");
        }
        if (repo.update(s) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return s;
    }

    @DeleteMapping("/{prn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String prn) {
        if (repo.delete(prn) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }
}
