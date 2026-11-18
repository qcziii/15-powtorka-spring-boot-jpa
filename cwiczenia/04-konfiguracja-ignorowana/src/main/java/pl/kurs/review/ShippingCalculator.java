package pl.kurs.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Shipping price from the configuration (shop.shipping.*). */
@Component
public class ShippingCalculator {

    @Value("${shop.shipping.price}")
    private BigDecimal price;

    @Value("${shop.shipping.free-from}")
    private static BigDecimal freeFrom;

    public BigDecimal shippingFor(BigDecimal orderValue) {
        return orderValue.compareTo(freeFrom) >= 0 ? BigDecimal.ZERO.setScale(2) : price;
    }
}
