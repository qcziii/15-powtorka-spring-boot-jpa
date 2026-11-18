package pl.kurs.review;

import java.math.BigDecimal;

public record OrderLine(String product, int quantity, BigDecimal price) {
}
