package MessageFactory.Model;

import lombok.Getter;

import static MessageFactory.Model.MessageType.REQUEST;

@Getter
public class MessageRequest extends MessageProjection {
    private final int index;
    private final int begin;
    private final int length;

    public MessageRequest(int index, int begin, int length) {
        super(REQUEST, index, begin, length);
        this.index = index;
        this.begin = begin;
        this.length = length;
    }
}
