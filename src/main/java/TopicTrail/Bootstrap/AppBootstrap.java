package TopicTrail.Bootstrap;

import TopicTrail.Domain.Group;
import TopicTrail.Domain.Role;
import TopicTrail.Domain.User;
import TopicTrail.Services.GroupService;
import TopicTrail.Services.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AppBootstrap implements ApplicationListener<ContextRefreshedEvent> {
    private final UserService userService;
    private final GroupService groupService;

    public AppBootstrap(UserService userService, GroupService groupService){
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event){
        System.out.println("DADA");
        if(Boolean.FALSE.equals(userService.existsByUsername("admin").block())){
            loadAdmin();
        }
    }

    public void loadAdmin(){
        System.out.println("ADMIN");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@gmail.com");
        user.setRoles(List.of(Role.ROLE_USER, Role.ROLE_ADMIN));
        userService.save(user).block();

        Group group = new Group();
        group.setApproved(true);
        group.setTitle("Grup0");
        group.setDescription("Descriere grup 0");
        groupService.save(group).block();

        return;
    }
}
