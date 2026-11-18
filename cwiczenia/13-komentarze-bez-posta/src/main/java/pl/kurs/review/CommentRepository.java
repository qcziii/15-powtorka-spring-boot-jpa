package pl.kurs.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Comments of the post (by the post_id column of the comment table). */
    List<Comment> findByPostId(Long postId);
}
