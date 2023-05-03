package TopicTrail.Repositories;

import TopicTrail.Domain.Post;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Mono<Post> findByTitle(String title);
    @Query("{'id' : ?0}")
    Mono<Post> findById(String id);
    Flux<Post> findByTitleContainsIgnoreCase(String text);
}
