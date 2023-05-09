package TopicTrail.Services;

import TopicTrail.Domain.Group;
import reactor.core.publisher.Mono;

public interface GroupService {
    Mono<Group> save(Group group);
    Mono<Void> delete(String Id);

}
