package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Builds a text report of an order (one line per product + total). */
@Service
public class ReportService {

    private final List<String> lines = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    public String report(String customer, List<OrderLine> order) {
        lines.add("Klient: " + customer);
        for (OrderLine line : order) {
            BigDecimal value = line.price().multiply(BigDecimal.valueOf(line.quantity()));
            lines.add(line.product() + " x" + line.quantity() + " = " + value);
            total = total.add(value);
        }
        lines.add("Razem: " + total);
        return String.join("\n", lines);
    }
}
