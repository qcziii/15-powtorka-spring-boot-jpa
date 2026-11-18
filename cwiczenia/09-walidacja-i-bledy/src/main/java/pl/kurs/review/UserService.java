package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Users kept in memory. This class is correct: do not change it. */
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public User register(UserRequest request) {
        boolean taken = users.values().stream().anyMatch(u -> u.email().equalsIgnoreCase(request.email()));
        if (taken) {
            throw new EmailTakenException(request.email());
        }
        long id = ids.incrementAndGet();
        User user = new User(id, request.name(), request.email(), request.age());
        users.put(id, user);
        return user;
    }

    public User get(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public void deleteAll() {
        users.clear();
    }
}
