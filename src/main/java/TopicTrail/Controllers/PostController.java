package TopicTrail.Controllers;

import TopicTrail.Domain.Post;
import TopicTrail.Domain.User;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.PostService;
import TopicTrail.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@ResponseBody
@CrossOrigin(origins = {"exp://192.168.0.105:19001", "exp://192.168.0.105:19000"}, exposedHeaders = "Authorization", allowCredentials = "true")
public class PostController {
    private final PostService postService;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    public PostController(PostService postService, JWTUtil jwtUtil, UserService userService) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
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
}
