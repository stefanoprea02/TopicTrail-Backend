package TopicTrail.Repositories;

import TopicTrail.Domain.Group;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GroupRepository extends ReactiveMongoRepository<Group, String> {
    Mono<Group> save(Group group);
    Mono<Void> delete(String Id);

}
