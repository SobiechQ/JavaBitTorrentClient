package TCP;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Represents a single entity  of peer-client communication. Can be used to represent either input or output value.
 * <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">Bep 003 impl</a>
 * For establishing handshaes use {@link Handshake Handkshake class}
 *
 */
@Getter
public class PeerMessage {
    private final byte[] data;
    private final MessageType messageType;

    public PeerMessage(@NonNull MessageType messageType, byte @Nullable [] data) {
        this.messageType = messageType;
        this.data = data != null ? data : new byte[64];
    }

    public PeerMessage(@NonNull MessageType messageType) {
        this(messageType, null);
    }
    
    public static PeerMessage get(@NonNull InputStream is) throws IOException {
        final var receivedLength= is.readNBytes(4);
        final int length = PeerMessage.bytesToInt(receivedLength);
        System.out.println(length);
        System.out.print(Arrays.toString(receivedLength));

        final var messageType = MessageType.valueOf((byte) is.read());
        System.out.println("-> " + messageType);

        final var receivedBody = is.readNBytes(length-1); //todo -1???
        return new PeerMessage(messageType, receivedBody);
    }

    public void send(@NonNull OutputStream os) throws IOException {
        os.write(new byte[]{this.messageType.getValue()});
        os.write(this.data);
        os.flush();
    }

    private static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (byte aByte : bytes) {
            result <<= 8;
            result |= (aByte & 0xFF);
        }
        return result;
    }
    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    @Override
    public String toString() {
        return "TCP.PeerMessage{" +
               "data=" + Arrays.toString(data) +
               ", messageType=" + messageType +
               '}';
    }
}
