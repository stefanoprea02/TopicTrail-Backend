package TopicTrail.Services;

import TopicTrail.Domain.Post;
import TopicTrail.Domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Flux<Post> findByTitle(String title);
    Mono<Post> findById(String id);
    Mono<Void> deleteById(String id);
    Flux<Post> getPosts();
    Mono<Post> save(Post post);
    Mono<Post> update(Post post);

    Boolean checkFavorite(User user, Post post);
}
