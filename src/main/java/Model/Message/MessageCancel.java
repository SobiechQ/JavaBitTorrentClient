package Model.Message;

import static Model.Message.MessageType.CANCEL;

public class MessageCancel extends MessageProjection{
    MessageCancel() {
        super(CANCEL);
    }
}
