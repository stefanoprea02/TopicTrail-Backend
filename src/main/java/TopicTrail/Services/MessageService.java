package TopicTrail.Services;

import TopicTrail.Domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessageService {
    Flux<Message> findBySender(String sender);
    Flux<Message> findByReceiver(String receiver);
    Flux<Message> findBySenderOrReceiver(String name);
    Mono<Message> save(Message message);
    Mono<Message> update(Message message);
}
