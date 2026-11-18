package pl.kurs.review;

public class InvalidRowException extends RuntimeException {

    public InvalidRowException(String message) {
        super(message);
    }
}
