package pl.kurs.review;

import java.math.BigDecimal;

public record ProductRequest(String name, String category, BigDecimal price) {
}
