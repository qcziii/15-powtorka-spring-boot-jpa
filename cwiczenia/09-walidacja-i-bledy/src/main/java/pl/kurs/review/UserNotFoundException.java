package pl.kurs.review;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("Nie ma użytkownika o id " + id);
    }
}
