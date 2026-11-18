package pl.kurs.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Foreign transfer order. */
public record TransferRequest(
        @NotBlank String recipient,
        @Pattern(regexp = "[A-Z]{3}") String currency,
        @Positive BigDecimal amount) {
}
