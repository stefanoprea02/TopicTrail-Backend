package TopicTrail.Controllers;

import TopicTrail.Domain.Comment;
import TopicTrail.Services.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Controller
@ResponseBody
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments/{username}")
    public Flux<?> getComments(@PathVariable String username){
        Flux<Comment> comments = commentService.getComments();
        comments = comments.filter(comm -> comm.getUsername().equals(username));
        return comments;
    }
}
