package Model.Message;

import static Model.Message.MessageType.CHOKE;

public class MessageChoke extends MessageProjection {
    MessageChoke() {
        super(CHOKE);
    }
}
