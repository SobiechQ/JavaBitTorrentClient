package Message.Model;

import lombok.Getter;

import static Message.Model.MessageType.HAVE;

@Getter
public class PeerMessageHave extends PeerMessageProjection {
    private final int index;

    public PeerMessageHave(int index) {
        super(HAVE, index);
        this.index = index;
    }
}
