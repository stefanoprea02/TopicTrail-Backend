package TopicTrail.Controllers;

import TopicTrail.Domain.Group;
import TopicTrail.Domain.Post;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.GroupService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@ResponseBody
public class GroupController {
    private final GroupService groupService;
    private final JWTUtil jwtUtil;

    public GroupController(GroupService groupService, JWTUtil jwtUtil) {
        this.groupService = groupService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/group/new")
    public Mono<Group> newGroup(@Valid Group g, @RequestHeader(name = "Authorization") String authorizationHeader){
        g.setId(UUID.randomUUID().toString());
        return groupService.save(g);
    }

    @GetMapping("/group/delete/{id}")
    public Mono<Void>  deleteGroup(String id){
        return groupService.delete(id);
    }

}
