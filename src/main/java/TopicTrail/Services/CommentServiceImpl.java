package TopicTrail.Services;

import TopicTrail.Domain.Comment;
import TopicTrail.Repositories.CommentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    @Override
    public Flux<Comment> getComments() {
        return commentRepository.findAll();
    }
}
