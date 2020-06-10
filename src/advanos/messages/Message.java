package advanos.messages;

import advanos.Host;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Message {
    Host source;
    String message;
}
