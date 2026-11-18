package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Notes kept in memory. This class is ready: do not change it. */
@Service
public class NoteService {

    private final Map<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public Note create(NoteRequest request) {
        long id = ids.incrementAndGet();
        Note note = new Note(id, request.title(), request.content());
        notes.put(id, note);
        return note;
    }

    public Optional<Note> findById(long id) {
        return Optional.ofNullable(notes.get(id));
    }

    /** All notes, sorted by id. */
    public List<Note> findAll() {
        return notes.values().stream().sorted(Comparator.comparingLong(Note::id)).toList();
    }

    /** Notes whose title contains the phrase (ignoring case), sorted by id. */
    public List<Note> search(String phrase) {
        String lower = phrase.toLowerCase();
        return findAll().stream().filter(n -> n.title().toLowerCase().contains(lower)).toList();
    }

    /** Replaces the note. Empty Optional when there is no note with this id. */
    public Optional<Note> update(long id, NoteRequest request) {
        if (!notes.containsKey(id)) {
            return Optional.empty();
        }
        Note note = new Note(id, request.title(), request.content());
        notes.put(id, note);
        return Optional.of(note);
    }

    /** Deletes the note. false when there was no note with this id. */
    public boolean delete(long id) {
        return notes.remove(id) != null;
    }

    public void deleteAll() {
        notes.clear();
    }
}
