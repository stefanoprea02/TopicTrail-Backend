package TopicTrail.Services;

import TopicTrail.Domain.Group;
import TopicTrail.Domain.Post;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {
    Mono<Group> save(Group group);
    Mono<Group> findById(String id);
    Mono<Void> delete(String Id);
    Mono<Group> findByTitle(String title);
    Flux<Group> findByTitleContainsIgnoreCase(String title);
    Flux<Group> findByDescriptionContainsIgnoreCase(String description);
    Flux<Group> getGroups();
    Mono<Group> update(Group group);
}
