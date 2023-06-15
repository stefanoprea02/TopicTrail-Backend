package TopicTrail.Services;

import TopicTrail.Domain.Group;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {
    Mono<Group> save(Group group);
    Mono<Void> delete(String Id);
    Mono<Group> findByTitle(String title);
    Flux<Group> findByTitleContainsIgnoreCase(String title);
    Flux<Group> getGroups();
    Mono<Group> update(Group group);
}
