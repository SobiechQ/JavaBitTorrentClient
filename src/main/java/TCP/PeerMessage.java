package TCP;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static TCP.MessageType.*;

/**
 * Represents a single entity  of peer-client communication. Can be used to represent either input or output value.
 * <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">Bep 003 impl</a>
 * For establishing handshaes use {@link Handshake Handkshake class}
 *
 */
@Getter
public class PeerMessage {
    private final static Logger logger = LogManager.getLogger();
    private final byte[] data;
    private final MessageType messageType;

    public PeerMessage(@NonNull MessageType messageType, byte @Nullable [] data) {
        this.messageType = messageType;
        this.data = data != null ? data : new byte[0];
    }

    public PeerMessage(@NonNull MessageType messageType) {
        this(messageType, (byte[]) null);
    }

    public PeerMessage(@NonNull MessageType messageType, int... data) { //todo test that splits to 4
        this(messageType,  intsToBytes(data));

    }
    
    public static PeerMessage get(@NonNull InputStream is) throws IOException {

        final int length = Try.of(()->is)
                .mapTry(s -> s.readNBytes(4))
                .map(PeerMessage::bytesToInt)
                .getOrElseThrow(() -> new IOException("Unable to read length"));

        if (length == 0) {
            final var peerMessage = new PeerMessage(KEEP_ALIVE);
            logger.info("Received: {}", peerMessage);
            return peerMessage;
        }

        final var messageType = MessageType.valueOf((byte) is.read());
        final var receivedBody = is.readNBytes(length - 1);

        final var peerMessage = new PeerMessage(messageType, receivedBody);
        logger.info("Received: {}", peerMessage);
        return peerMessage;
    }

    public int getLength() {
        return this.data.length + (this.messageType == KEEP_ALIVE ? 0 : 1);
    }

    public void send(@NonNull OutputStream os) throws IOException {
        os.write(intToBytes(this.getLength()));

        if (this.messageType != KEEP_ALIVE) { //Keep alives have no value representing their messageType and their body is empty.
            os.write(new byte[]{this.messageType.getValue()});
            os.write(this.data);
        }

        os.flush();
        logger.info("Sent: {}", this);
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
    private static Stream<Byte> bytesToStream(byte[] bytes){
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> bytes[i]);
    }

    private static byte[] intsToBytes(int[] data) {
        return Arrays.stream(data)
                .boxed()
                .map(PeerMessage::intToBytes)
                .flatMap(PeerMessage::bytesToStream)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            byte[] arr = new byte[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                arr[i] = list.get(i);
                            }
                            return arr;
                        }

                ));
    }

    @Override
    public String toString() {
        return "TCP.PeerMessage{" +
               "messageType=" + messageType +
               ", length=" + this.getLength() +
               ", data=" + Arrays.toString(data) +
               '}';
    }
}
