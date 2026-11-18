package pl.kurs.review;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movies;

    public MovieService(MovieRepository movies) {
        this.movies = movies;
    }

    /** Movie with exactly this title. */
    public Optional<Movie> byTitle(String title) {
        return movies.findByName(title).stream().findFirst();
    }

    /** Titles of the movies of directors whose name contains the phrase (ignoring case), sorted. */
    public List<String> byDirector(String phrase) {
        return movies.findByDirectorContaining(phrase).stream().map(Movie::getTitle).sorted().toList();
    }

    /** Titles of the movies released in the given year or later, the best rated first. */
    public List<String> releasedSince(int year) {
        return movies.releasedSince(year).stream().map(Movie::getTitle).toList();
    }
}
