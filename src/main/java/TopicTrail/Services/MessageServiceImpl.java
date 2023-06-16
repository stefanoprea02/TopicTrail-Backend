package TopicTrail.Services;

import TopicTrail.Domain.Message;
import TopicTrail.Repositories.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Flux<Message> findBySender(String sender) {
        return messageRepository.findBySender(sender);
    }

    @Override
    public Flux<Message> findByReceiver(String receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    @Override
    public Flux<Message> findBySenderOrReceiver(String name) {
        Flux<Message> byReceiver = messageRepository.findByReceiver(name);
        Flux<Message> bySender = messageRepository.findBySender(name);

        Flux<Message> merged = Flux.merge(byReceiver, bySender);

        // Sort the merged Flux by LocalDateTime
        Flux<Message> sorted = merged.sort((msg1, msg2) -> {
            LocalDateTime timestamp1 = msg1.getDateTime();
            LocalDateTime timestamp2 = msg2.getDateTime();
            return timestamp1.compareTo(timestamp2);
        });

        return sorted;
    }

    @Override
    public Mono<Message> save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Mono<Message> update(Message message) {
        return messageRepository.findById(message.getId())
                .map(m -> message)
                .flatMap(messageRepository::save);
    }
}
