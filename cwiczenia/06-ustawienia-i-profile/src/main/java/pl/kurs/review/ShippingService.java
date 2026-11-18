package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingService {

    private final ShopProperties properties;

    public ShippingService(ShopProperties properties) {
        this.properties = properties;
    }

    public BigDecimal shippingFor(String country, BigDecimal orderValue) {
        if (!properties.countries().contains(country)) {
            throw new IllegalArgumentException("Nie wysyłamy do kraju " + country);
        }
        if (orderValue.compareTo(properties.shipping().freeFrom()) >= 0) {
            return BigDecimal.ZERO;
        }
        return properties.shipping().price();
    }

    public String shopName() {
        return properties.name();
    }
}
