package pl.kurs.review;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String account) {
        super("Brak środków na koncie " + account);
    }
}
