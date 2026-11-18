package pl.kurs.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByName(String name);

    List<Movie> findByDirectorContaining(String phrase);

    /** Movies released in the given year or later, the best rated first. */
    @Query("select m from movies m where m.releaseYear >= :year order by m.rating desc")
    List<Movie> releasedSince(int year);
}
