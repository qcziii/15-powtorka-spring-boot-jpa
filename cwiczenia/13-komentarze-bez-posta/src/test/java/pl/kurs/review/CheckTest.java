package pl.kurs.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
class CheckTest {

    @Autowired
    BlogService blog;
    @Autowired
    PostRepository posts;
    @Autowired
    CommentRepository comments;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        posts.deleteAll();
    }

    @Test
    @DisplayName("1) Komentarz ma w bazie wypełnioną kolumnę post_id")
    void foreignKeyFilled() {
        Long postId = blog.createPost("Pierwszy post");
        Long commentId = blog.addComment(postId, "Super!");
        assertNotNull(commentId, "id komentarza");
        assertEquals(postId, jdbc.queryForObject("select post_id from comment where id = ?", Long.class, commentId),
                "post_id komentarza w bazie");
    }

    @Test
    @DisplayName("2) findByPostId zwraca komentarze posta")
    void findByPost() {
        Long first = blog.createPost("Post A");
        Long second = blog.createPost("Post B");
        blog.addComment(first, "a1");
        blog.addComment(first, "a2");
        blog.addComment(second, "b1");
        assertEquals(List.of("a1", "a2"), comments.findByPostId(first).stream().map(Comment::getText).sorted().toList());
    }

    @Test
    @DisplayName("3) Relacja jest zapisana tylko w kolumnie post_id: nie ma dodatkowej tabeli łączącej")
    void noJoinTable() {
        Integer tables = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'PUBLIC'", Integer.class);
        assertEquals(2, tables, "Liczba tabel w bazie (powinny być tylko post i comment). Tabele: "
                + jdbc.queryForList("select table_name from information_schema.tables where table_schema = 'PUBLIC'", String.class));
    }
}
