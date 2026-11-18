package pl.kurs.review;

import java.util.List;

public record ReportRequest(String customer, List<OrderLine> lines) {
}
