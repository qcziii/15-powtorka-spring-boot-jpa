package pl.kurs.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/** Audit log entry: who tried to do what. Entries must stay even when the operation itself failed. */
@Entity
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    protected AuditEntry() {
    }

    public AuditEntry(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
