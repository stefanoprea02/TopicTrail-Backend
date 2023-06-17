package TopicTrail.Domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @Field("_id")
    private String id = UUID.randomUUID().toString();
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 50, message = "Must be between 3 and 20 characters long")
    private String title;
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 50, message = "Must be between 3 and 50 characters long")
    private String content;
    private LocalDate postCreatedAt = LocalDate.now();
    private String username;
    @NotBlank(message = "Must not be blank")
    private String group;
    private Set<String> comments = new HashSet<>();
}
