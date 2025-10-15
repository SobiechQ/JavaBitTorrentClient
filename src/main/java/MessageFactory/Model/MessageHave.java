package MessageFactory.Model;

import lombok.Getter;

import static MessageFactory.Model.MessageType.HAVE;

@Getter
public class MessageHave extends MessageProjection {
    private final int index;

    public MessageHave(int index) {
        super(HAVE, index);
        this.index = index;
    }
}
