package pl.kurs.review;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;

/** Shop settings from application.yml (prefix "shop"). */
@ConfigurationProperties(prefix = "shop")
public record ShopProperties(String name, Shipping shipping, List<String> countries) {

    public record Shipping(BigDecimal price, BigDecimal freeFrom) {
    }
}
