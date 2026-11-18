package pl.kurs.review;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Registration form. Rules: name not blank, e-mail not blank and valid, age at least 18.
 */
public record UserRequest(
        @NotNull String name,
        @NotNull @Email String email,
        @Min(18) int age) {
}
