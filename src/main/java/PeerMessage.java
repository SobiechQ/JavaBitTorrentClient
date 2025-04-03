import lombok.NonNull;

import java.awt.*;

public class PeerMessage {
    private final byte[] data;

    public PeerMessage(byte @NonNull [] data) {
        if (data.length < 5)
            throw new IllegalArgumentException("Message data too short");
        this.data = data;
    }

    public MessageType getMessageType() {
        return MessageType.valueOf(this.data[5]);
    }


}
