package studentrecord;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Model class: one student record. */
public record Student(
        @NotBlank String prn,
        @NotBlank String name,
        @NotBlank String course,
        @NotNull @Min(1) @Max(6) Integer year,
        String phone) {

    /** Returns a copy with all text fields trimmed (phone never null). */
    public Student trimmed() {
        return new Student(
                prn == null ? null : prn.trim(),
                name == null ? null : name.trim(),
                course == null ? null : course.trim(),
                year,
                phone == null ? "" : phone.trim());
    }
}
