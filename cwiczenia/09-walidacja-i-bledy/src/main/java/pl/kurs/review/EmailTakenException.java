package pl.kurs.review;

public class EmailTakenException extends RuntimeException {

    public EmailTakenException(String email) {
        super("E-mail jest już zajęty: " + email);
    }
}
