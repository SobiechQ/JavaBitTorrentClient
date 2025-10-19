package MessageFactory.Model;

import lombok.Getter;

import static MessageFactory.Model.MessageType.REQUEST;

@Getter
public class MessageRequest extends MessageProjection {
    private final static int REQUEST_LENGTH = (int) Math.pow(2, 14);
    private final int index;
    private final int begin;
    private final int length;

    public MessageRequest(int index, int begin, int length) {
        super(REQUEST, index, begin, length);
        this.index = index;
        this.begin = begin;
        this.length = length;
    }

    public MessageRequest(int index, int begin) {
        this(index, begin, REQUEST_LENGTH);
    }
}
