package TopicTrail.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @Field("_id")
    private String id = UUID.randomUUID().toString();
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 20, message = "Must be between 3 and 20 characters long")
    private String username;
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 20, message = "Must be between 3 and 20 characters long")
    private String password;
    @NotBlank(message = "Must not be blank")
    @Email(message = "Must be a valid email")
    private String email;
    private List<Role> roles;
    private String token;
    private Set<String> favorites = new HashSet<>();
    private Set<String> moderating = new HashSet<>();

    public Set<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<String> favorites) {
        this.favorites = favorites;
    }

    private Set<String> posts = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
