package TopicTrail.Controllers;

import TopicTrail.Domain.Comment;
import TopicTrail.Domain.Post;
import TopicTrail.Domain.User;
import TopicTrail.Repositories.CommentRepository;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.PostService;
import TopicTrail.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@ResponseBody
public class PostController {
    private final PostService postService;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    private final CommentRepository commentRepository;

    public PostController(PostService postService, JWTUtil jwtUtil, UserService userService, CommentRepository commentRepository) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.commentRepository=commentRepository;
    }

    @PostMapping("/post/new")
    public Mono<Post> newPost(@Valid Post post, @RequestHeader(name = "Authorization") String authorizationHeader){
        if(post.getId().length() != 36){
            post.setId(UUID.randomUUID().toString());

            authorizationHeader = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(authorizationHeader);
            post.setUsername(username);

            Mono<User> user = userService.findByUsername(username);

            user.flatMap(u -> {
                u.getPosts().add(post.getId());
                return userService.save(u);
            });

            Mono<Post> savedPost = postService.save(post);

            return savedPost;
        }else
            return postService.update(post);
    }

    @GetMapping("/post/all")
    Flux<Post> getPosts(){
        return postService.getPosts();
    }

    @GetMapping("/post/all?nume={var}")
    Flux<Post> getPostsSearch(@PathVariable String var) {
        return postService.findByTitle(var);
    }

    @GetMapping("/post/{postId}/comment")
    public Flux<Comment> getComments(@PathVariable String postId){
        return commentRepository.findByPostId(postId);
    }

    @PostMapping("/post/{postId}/comment/new")
    public Mono<Comment> newComment(@PathVariable String postId, @RequestBody Comment comment, @RequestHeader(name="Authorization") String authorizationHeader){
        if(comment.getId()==null){
            comment.setId(UUID.randomUUID().toString());
        }
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDate.now());

        authorizationHeader=authorizationHeader.substring(7);
        String username= jwtUtil.getUsernameFromToken(authorizationHeader);
        comment.setUsername(username);

        Mono<Comment> savedComment=commentRepository.save(comment);

        return savedComment;
    }
}