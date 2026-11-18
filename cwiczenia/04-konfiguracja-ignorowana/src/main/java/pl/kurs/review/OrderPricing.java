package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderPricing {

    private final DiscountPolicy discount = new DiscountPolicy();
    private final ShippingCalculator shipping;

    public OrderPricing(ShippingCalculator shipping) {
        this.shipping = shipping;
    }

    /** Value of the goods after the discount. */
    public BigDecimal goods(BigDecimal listPrice) {
        return discount.apply(listPrice);
    }

    /** Amount to pay: goods after the discount + shipping. */
    public BigDecimal total(BigDecimal listPrice) {
        BigDecimal goods = goods(listPrice);
        return goods.add(shipping.shippingFor(goods));
    }
}
