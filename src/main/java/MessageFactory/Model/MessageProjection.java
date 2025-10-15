package MessageFactory.Model;

import Utils.ByteUtils;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Data
public class MessageProjection {
    private final MessageType messageType;
    private final byte[] payload;

    public MessageProjection(@NonNull MessageType messageType, byte @Nullable [] payload) {
        this.messageType = messageType;
        this.payload = payload == null ? new byte[0] : payload;
    }

    public MessageProjection(@NonNull MessageType messageType) {
        this(messageType,(byte[]) null);
    }

    public MessageProjection(@NonNull MessageType messageType, int... data) {
        this(messageType,  ByteUtils.intsToBytes(data));
    }

    public byte[] getData() {
        if (this.getMessageType().equals(MessageType.KEEP_ALIVE))
            return new byte[]{0, 0, 0, 0};

        final var data = new byte[4 + 1 + payload.length];
        final var lengthPrefix = getLengthPrefix();

        System.arraycopy(lengthPrefix, 0, data, 0, 4);
        data[4] = messageType.getValue();
        System.arraycopy(payload, 0, data, 5, payload.length);

        return data;
    }

    private byte[] getLengthPrefix() {
        return ByteUtils.intToBytes(1 + payload.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageProjection that = (MessageProjection) o;
        return messageType == that.messageType && Objects.deepEquals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageType, Arrays.hashCode(payload));
    }
}
