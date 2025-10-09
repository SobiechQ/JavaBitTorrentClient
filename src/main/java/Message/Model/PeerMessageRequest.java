package Message.Model;

import lombok.Getter;

import static Message.Model.MessageType.REQUEST;

@Getter
public class PeerMessageRequest extends PeerMessageProjection {
    private final int index;
    private final int begin;
    private final int length;

    public PeerMessageRequest(int index, int begin, int length) {
        super(REQUEST, index, begin, length);
        this.index = index;
        this.begin = begin;
        this.length = length;
    }
}
