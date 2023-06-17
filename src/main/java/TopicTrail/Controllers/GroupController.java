package TopicTrail.Controllers;

import TopicTrail.Domain.Group;
import TopicTrail.Domain.Role;
import TopicTrail.Domain.User;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.GroupService;
import TopicTrail.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@ResponseBody
public class GroupController {
    private final GroupService groupService;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    public GroupController(GroupService groupService, JWTUtil jwtUtil, UserService userService) {
        this.groupService = groupService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/group/new")
    public Mono<Group> newGroup(@Valid Group g, @RequestHeader(name = "Authorization") String authorizationHeader){
        g.setId(UUID.randomUUID().toString());
        return groupService.save(g);
    }

    @GetMapping("/group/delete/{id}")
    public Mono<Void> deleteGroup(@PathVariable String id){
        return groupService.delete(id);
    }

    @GetMapping("/group/all")
    public Flux<Group> getGroupsSearch(@RequestParam("groupTitle") String groupTitle){
        Flux<Group> groupFlux = groupService.findByTitleContainsIgnoreCase(groupTitle);
        return groupFlux.filter(g -> g.getApproved().equals(true));
    }

    @GetMapping("/group/allnotapproved")
    public Flux<Group> getGroupsNotApproved(){
        Flux<Group> groupFlux = groupService.getGroups();
        return groupFlux.filter(g -> g.getApproved().equals(false));
    }

    @GetMapping("/group/{title}")
    public Mono<Group> getGroup(@PathVariable String title){
        return groupService.findByTitle(title);
    }

    @GetMapping("/group/approve/{title}")
    public Mono<?> approveGroup(@PathVariable String title, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        List<String> roles = (List<String>) jwtUtil.getAllClaimsFromToken(token).get("role");
        if(roles.contains("ROLE_ADMIN")){
            Mono<Group> groupMono = groupService.findByTitle(title);
            return groupMono.flatMap(g -> {
                System.out.println("DA");
                g.setApproved(true);
                return groupService.update(g);
            });
        }
        return Mono.empty();
    }

    @GetMapping("/group/disapprove/{id}")
    public Mono<?> disapproveGroup(@PathVariable String id, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        List<String> roles = (List<String>) jwtUtil.getAllClaimsFromToken(token).get("role");
        if(roles.contains("ROLE_ADMIN")){
            return groupService.delete(id);
        }
        return Mono.empty();
    }

    @GetMapping("/group/join/{groupId}")
    public Mono<?> joinGroup(@PathVariable String groupId, @RequestHeader(name = "Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return user.flatMap(user1 -> {
            user1.getGroups().add(groupId);
            return userService.update(user1);
        });
    }

    // Metoda pentru a părăsi un grup
    @GetMapping("/group/leave/{groupId}")
    public Mono<?> leaveGroup(@PathVariable String groupId, @RequestHeader(name = "Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        return user.flatMap(user1 -> {
            user1.getGroups().remove(groupId);
            return userService.update(user1);
        });
    }
    @GetMapping("/group/checkGroup/{groupId}")
    public Mono<Boolean> checkGroup(@PathVariable String groupId, @RequestHeader(name = "Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Mono<User> user = userService.findByUsername(jwtUtil.getUsernameFromToken(token));

        user = user.filter(user1 -> user1.getGroups().contains(groupId));

        return user.hasElement();
    }
}
