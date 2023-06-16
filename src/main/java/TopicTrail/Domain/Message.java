package TopicTrail.Domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Message {
    @Id
    @Field("_id")
    private String id = UUID.randomUUID().toString();
    @NotBlank
    private String sender;
    @NotBlank
    private String receiver;
    private LocalDateTime dateTime = LocalDateTime.now();
    @NotBlank(message = "Must not be blank")
    private String content;
    private Boolean isSent = false;
}
