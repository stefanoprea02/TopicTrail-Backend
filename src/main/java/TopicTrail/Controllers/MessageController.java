package TopicTrail.Controllers;

import TopicTrail.Domain.Message;
import TopicTrail.Domain.User;
import TopicTrail.Services.MessageService;
import TopicTrail.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.*;

import java.util.UUID;

@Controller
@ResponseBody
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    private final Sinks.Many<Message> chatSink;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
        this.chatSink = Sinks.many().multicast().directBestEffort();
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "/messages/{receiver}")
    public Flux<Message> getMessages(@PathVariable String receiver){
        Flux<Message> messageFlux = messageService.findBySenderOrReceiver(receiver);

        return Flux.concat(messageFlux, chatSink.asFlux().filter(msg -> {
                    return msg.getReceiver().equals(receiver) || msg.getSender().equals(receiver);
                })
                .map(msg -> {
                    if(msg.getReceiver().equals(receiver)){
                        msg.setIsSent(true);
                        messageService.save(msg);
                    }
                    return msg;
                }));
    }

    @GetMapping("/messages/all/{receiver}")
    public Flux<Message> getAllMessages(@PathVariable String receiver){
        return messageService.findBySenderOrReceiver(receiver);
    }

    @PostMapping("/messages")
    public Mono<Message> postMessage(@Valid Message message){
        if(message.getId().length() != 36)
            message.setId(UUID.randomUUID().toString());

        chatSink.emitNext(message, ((signalType, emitResult) -> emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED));

        return messageService.save(message);
    }
}
