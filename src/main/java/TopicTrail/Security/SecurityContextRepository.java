package TopicTrail.Security;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private AuthenticationManager authenticationManager;


    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authToken == null || authToken.isBlank()) {
            return Mono.empty();
        }
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(authToken, authToken);
        return this.authenticationManager.authenticate(authentication).map(SecurityContextImpl::new);
    }
}
