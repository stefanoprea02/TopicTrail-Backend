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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @Field("_id")
    private String id = UUID.randomUUID().toString();
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 20, message = "Must be between 3 and 20 characters long")
    private String title;
    private String image;
    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 20, message = "Must be between 3 and 20 characters long")
    private String description;
    private Set<String> posts = new HashSet<>();
    private Boolean approved = false;
}
