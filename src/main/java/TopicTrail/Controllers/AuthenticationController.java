package TopicTrail.Controllers;

import TopicTrail.Domain.AuthRequest;
import TopicTrail.Domain.Role;
import TopicTrail.Domain.User;
import TopicTrail.Security.JWTUtil;
import TopicTrail.Services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JWTUtil jwtUtil;
    private final UserService userService;

    public AuthenticationController(JWTUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity> login(Mono<AuthRequest> authRequestMono){
        System.out.println("login");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return authRequestMono.flatMap(login -> {
            return userService.findByUsername(login.getUsername())
                    .filter(user -> passwordEncoder.matches(login.getPassword(), user.getPassword()))
                    .map(user -> {
                        String token = jwtUtil.generateToken(user);

                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

                        return ResponseEntity.ok().headers(httpHeaders).body(token);
                    }).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
        });
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(name = "Authorization") String authorizationHeader){
        System.out.println("validate");
        try{
            String token = authorizationHeader.substring(7); // remove the "Bearer " prefix
            Boolean isValid = jwtUtil.validateToken(token);
            return ResponseEntity.ok(isValid);
        }catch (ExpiredJwtException e){
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/checkUsername/{username}")
    public Mono<ResponseEntity<Boolean>> checkUsername(@PathVariable String username){
        System.out.println("DA");
        Mono<ResponseEntity<Boolean>> responseEntityMono = userService.existsByUsername(username)
                .map(x -> new ResponseEntity<>(x, HttpStatus.OK));

        return responseEntityMono;
    }

    @PostMapping("/register")
    public Mono<User> register(@Valid User user){
        System.out.println(user.getPassword());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(Role.ROLE_USER));

        return userService.save(user);
    }
}
