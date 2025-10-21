package Model.Message;

import static Model.Message.MessageType.KEEP_ALIVE;

public class MessageKeepAlive extends MessageProjection {
    MessageKeepAlive() {
        super(KEEP_ALIVE);
    }
}
