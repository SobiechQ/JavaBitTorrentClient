package Model.Message;

import static Model.Message.MessageType.CHOKE;
import static Model.Message.MessageType.INTERESTED;

public class MessageInterested extends MessageProjection {
    MessageInterested() {
        super(INTERESTED);
    }
}
