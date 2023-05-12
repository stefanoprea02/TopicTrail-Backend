package TopicTrail.Services;

import TopicTrail.Domain.Comment;
import TopicTrail.Domain.Post;
import TopicTrail.Repositories.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Flux<Post> findByTitle(String title) {
        return postRepository.findByTitleContainsIgnoreCase(title);
    }

    @Override
    public Mono<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return postRepository.deleteById(id);
    }

    @Override
    public Flux<Post> getPosts() {
        return postRepository.findAll();
    }

    @Override
    public Mono<Post> save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Mono<Post> update(Post post) {
        return postRepository.findById(post.getId())
                .map(u -> post)
                .flatMap(postRepository::save);
    }
    @Override
    public Mono<Post> addComment(String postId, String commentContent, String username) {
        return postRepository.findById(postId)
                .flatMap(post -> {
                    Comment comment = new Comment();
                    comment.setText(commentContent);
                    comment.setUsername(username);
                    post.getComments().add(comment);
                    return postRepository.save(post);
                });
    }
    @Override
    public Flux<Comment> getComments(String postId) {
        return postRepository.findById(postId)
                .flatMapIterable(Post::getComments);
    }


}
