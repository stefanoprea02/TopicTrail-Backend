package TopicTrail.Controllers;

import TopicTrail.Domain.User;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.UserService;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

@Controller
@ResponseBody
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user/{username}")
    public Mono<User> getUser(@PathVariable String username){
        return userService.findByUsername(username);
    }

    @GetMapping("/users/{username}")
    public Flux<User> getUsers(@PathVariable String username){
        return userService.findByUsernameLike(username);
    }

    @GetMapping("/user/isAdmin")
    public Mono<Boolean> isAdmin(@RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        List<String> roles = (List<String>) jwtUtil.getAllClaimsFromToken(token).get("role");
        if(roles.contains("ROLE_ADMIN")){
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @GetMapping("/user/isModerating/{groupName}")
    public Mono<Boolean> isModerating(@PathVariable String groupName, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> userMono = userService.findByUsername(jwtUtil.getUsernameFromToken(token));
        return userMono.flatMap(u -> {
            if(u.getModerating().contains(groupName))
                return Mono.just(true);
            else return Mono.just(false);
        });
    }

    @GetMapping("/user/addModerating/{user}/{group}")
    public Mono<?> addModerating(@PathVariable String user, @PathVariable String group, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        List<String> roles = (List<String>) jwtUtil.getAllClaimsFromToken(token).get("role");
        if(roles.contains("ROLE_ADMIN")){
            Mono<User> userMono = userService.findByUsername(user);
            return userMono.flatMap(u -> {
                u.getModerating().add(group);
                return userService.update(u);
            });
        }
        return Mono.empty();
    }

    @GetMapping("/user/removeModerating/{user}/{group}")
    public Mono<?> removeModerating(@PathVariable String user, @PathVariable String group, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        List<String> roles = (List<String>) jwtUtil.getAllClaimsFromToken(token).get("role");
        if(roles.contains("ROLE_ADMIN")){
            Mono<User> userMono = userService.findByUsername(user);
            return userMono.flatMap(u -> {
                u.getModerating().remove(group);
                return userService.update(u);
            });
        }
        return Mono.empty();
    }

    @PostMapping("/user/bio")
    public Mono changeBio(@RequestParam String bio, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> userMono = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return userMono.flatMap(user -> {
            user.setBio(bio);
            return userService.update(user);
        });
    }

    @PostMapping(value = "/user/changeImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono changeImage(@RequestPart("file") Mono<FilePart> file, @RequestHeader(name = "Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Mono<User> userMono = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return userMono.flatMap(user -> file.flatMap(this::readBase64Content)
                .doOnNext(profileImage -> user.setProfileImage(profileImage))
                .flatMap(profileImage -> userService.update(user).thenReturn(profileImage)));
    }

    public Mono<String> readBase64Content(FilePart filePart) {
        return filePart
                .content()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return bytes;
                })
                .reduce(new byte[0], (acc, bytes) -> {
                    byte[] result = new byte[acc.length + bytes.length];
                    System.arraycopy(acc, 0, result, 0, acc.length);
                    System.arraycopy(bytes, 0, result, acc.length, bytes.length);
                    return result;
                })
                .map(completeBytes -> Base64.getEncoder().encodeToString(completeBytes));
    }
}
