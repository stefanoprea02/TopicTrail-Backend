package TopicTrail.Services;

import TopicTrail.Domain.Comment;
import reactor.core.publisher.Flux;

public interface CommentService {
    Flux<Comment> getComments();
}
