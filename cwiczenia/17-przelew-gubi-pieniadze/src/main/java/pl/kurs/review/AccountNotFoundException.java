package pl.kurs.review;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String account) {
        super("Nie ma konta " + account);
    }
}
