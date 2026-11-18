package pl.kurs.review;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

/**
 * Exchange rates to PLN, read from the classpath file pl/kurs/review/rates.properties.
 */
public class ExchangeRates {

    private final Properties rates = new Properties();

    public ExchangeRates() {
        try (InputStream in = ExchangeRates.class.getResourceAsStream("/pl/kurs/review/rates.properties")) {
            if (in == null) {
                throw new IllegalStateException("Nie ma pliku pl/kurs/review/rates.properties na classpath");
            }
            rates.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Nie da się odczytać kursów walut", e);
        }
    }

    public BigDecimal toPln(String currency) {
        String rate = rates.getProperty(currency);
        if (rate == null) {
            throw new IllegalArgumentException("Nieznana waluta: " + currency);
        }
        return new BigDecimal(rate);
    }
}
