package pl.kurs.review;

/** The transfer looks like a fraud: it must be blocked and nothing may change. */
public class SuspiciousTransferException extends Exception {

    public SuspiciousTransferException(String message) {
        super(message);
    }
}
