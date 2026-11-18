package pl.kurs.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Discount from the configuration (shop.discount.percent). */
@Component
public class DiscountPolicy {

    @Value("${shop.discount.percent}")
    private int percent;

    public BigDecimal apply(BigDecimal amount) {
        BigDecimal factor = BigDecimal.valueOf(100 - percent).movePointLeft(2);
        return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
