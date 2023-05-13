package TopicTrail.Controllers;

import TopicTrail.Domain.User;
import TopicTrail.Services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@ResponseBody
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{username}")
    public Flux<User> getUser(@PathVariable String username){
        return userService.findByUsernameLike(username);
    }
}
