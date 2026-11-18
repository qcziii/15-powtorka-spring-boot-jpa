package pl.kurs.review;

import java.math.BigDecimal;

/** Partial change of a product (like PATCH): a null field means "do not change". */
public record ProductChange(String name, BigDecimal price, Integer stock) {
}
