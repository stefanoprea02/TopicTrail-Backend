package TopicTrail.Services;

import TopicTrail.Domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
    Mono<User> findById(String id);
    Mono<User> update(User user);
    Mono<User> save(User user);
    Flux<User> findByUsernameLike(String text);
}
