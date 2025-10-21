package Model.Message;

import static Model.Message.MessageType.UNCHOKE;

public class MessageUnchoke extends MessageProjection {
    MessageUnchoke() {
        super(UNCHOKE);
    }
}
