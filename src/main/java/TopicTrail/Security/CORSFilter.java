package TopicTrail.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableWebFlux
public class CORSFilter implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        String[] ip = new String[2];
        try{
            ip[0] = "exp://" + InetAddress.getLocalHost().getHostAddress() + "19000";
            ip[1] = "exp://" + InetAddress.getLocalHost().getHostAddress() + "19001";
        }catch (UnknownHostException error){
            System.out.println(error);
        }

        registry.addMapping("/**")
                .allowedOrigins(ip)
                .allowedMethods("GET", "POST")
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Set-Cookie");
    }
}
