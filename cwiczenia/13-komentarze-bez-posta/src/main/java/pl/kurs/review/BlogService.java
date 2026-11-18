package pl.kurs.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogService {

    private final PostRepository posts;

    public BlogService(PostRepository posts) {
        this.posts = posts;
    }

    @Transactional
    public Long createPost(String title) {
        return posts.save(new Post(title)).getId();
    }

    @Transactional
    public Long addComment(Long postId, String text) {
        Post post = posts.findById(postId).orElseThrow();
        Comment comment = new Comment(text);
        post.addComment(comment);
        posts.flush();
        return comment.getId();
    }
}
