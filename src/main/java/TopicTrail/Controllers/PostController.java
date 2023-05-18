package TopicTrail.Controllers;

import TopicTrail.Domain.Comment;
import TopicTrail.Domain.Group;
import TopicTrail.Domain.Post;
import TopicTrail.Domain.User;
import TopicTrail.Repositories.CommentRepository;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.GroupService;
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
    private final GroupService groupService;
    private final CommentRepository commentRepository;

    public PostController(PostService postService, JWTUtil jwtUtil, UserService userService, GroupService groupService, CommentRepository commentRepository) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.groupService = groupService;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/post/new")
    public Mono<Post> newPost(@Valid Post post, @RequestHeader(name = "Authorization") String authorizationHeader){
        if(post.getId().length() != 36){
            post.setId(UUID.randomUUID().toString());

            authorizationHeader = authorizationHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(authorizationHeader);
            post.setUsername(username);

            Mono<User> user = userService.findByUsername(username);
            Mono<Group> group = groupService.findByTitle(post.getGroup());

            user.flatMap(u -> {
                u.getPosts().add(post.getId());
                return userService.save(u);
            });

            group.flatMap(g -> {
                g.getPosts().add(post.getId());
                return groupService.save(g);
            });

            Mono<Post> savedPost = postService.save(post);

            return savedPost;
        }else
            return postService.update(post);
    }

    @GetMapping("/post/all")
    Flux<Post> getPosts(@RequestParam(required = false) String groupName, @RequestParam(required = false) String username,
                        @RequestParam(required = false) String favorite, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Flux<Post> posts = postService.getPosts();
        if(groupName != null)
            posts = posts.filter(x -> x.getGroup().equals(groupName));
        if(username != null)
            posts = posts.filter(x -> x.getUsername().equals(username));
        return posts;
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
    public Mono<Comment> newComment(@Valid Comment comment, @PathVariable String postId, @RequestHeader(name="Authorization") String authorizationHeader){
        if(comment.getId()==null){
            comment.setId(UUID.randomUUID().toString());
        }
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDate.now());

        authorizationHeader=authorizationHeader.substring(7);
        String username= jwtUtil.getUsernameFromToken(authorizationHeader);
        comment.setUsername(username);

        Mono<Post> post = postService.findById(postId);
        post.flatMap(p -> {
            p.getComments().add(comment.getId());
            return postService.save(p);
        });

        Mono<Comment> savedComment=commentRepository.save(comment);

        return savedComment;
    }

    @GetMapping("/post/adFavorite/{adId}")
    Mono<?> adFovorite(@PathVariable String adId, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return user.flatMap(user1 -> {
            user1.getFavorites().add(adId);
            return userService.update(user1);
        });
    }

    @GetMapping("/post/removeFavorite/{adId}")
    Mono<?> removeFavorite(@PathVariable String adId, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return user.flatMap(user1 -> {
            user1.getFavorites().remove(adId);
            return userService.update(user1);
        });
    }

    @GetMapping("/post/checkFavorite/{adId}")
    Mono<Boolean> checkFavorite(@PathVariable String adId, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        user = user.filter(user1 -> user1.getFavorites().contains(adId));

        return user.hasElement();
    }
}
