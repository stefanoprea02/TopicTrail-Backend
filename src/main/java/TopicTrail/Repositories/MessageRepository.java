package TopicTrail.Repositories;

import TopicTrail.Domain.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findBySender(String sender);
    Flux<Message> findByReceiver(String receiver);
}
