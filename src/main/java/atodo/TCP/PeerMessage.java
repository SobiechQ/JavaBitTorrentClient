package atodo.TCP;

import MessageFactory.Model.MessageType;
import Utils.ByteUtils;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;

import static MessageFactory.Model.MessageType.*;

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
        this(messageType,  ByteUtils.intsToBytes(data));
    }
    
    public static PeerMessage get(@NonNull InputStream is) throws IOException {
        final var bis = new BufferedInputStream(is);


        final int length = Try.of(()->bis)
                .mapTry(s -> s.readNBytes(4))
                .map(ByteUtils::bytesToInt)
                .getOrElseThrow(() -> new IOException("Unable to read length"));


        if (length == 0) {
            final var peerMessage = new PeerMessage(KEEP_ALIVE);
            logger.info("Received: {}", peerMessage);
            return peerMessage;
        }

        final MessageType messageType;
        messageType = MessageType.valueOf((byte) bis.read());
        final var receivedBody = bis.readNBytes(length - 1);

        final var peerMessage = new PeerMessage(messageType, receivedBody);
        logger.info("Received: {}", peerMessage);
        return peerMessage;
    }

    public int getLength() {
        return this.data.length + (this.messageType == KEEP_ALIVE ? 0 : 1);
    }

    public void send(@NonNull OutputStream os) throws IOException {
        os.write(ByteUtils.intToBytes(this.getLength()));

        if (this.messageType != KEEP_ALIVE) { //Keep alives have no value representing their messageType and their body is empty.
            os.write(new byte[]{this.messageType.getValue()});
            os.write(this.data);
        }

        os.flush();
        logger.info("Sent: {}", this);
    }



    @Override
    public String toString() {


        return "TCP.PeerMessage{" +
               "messageType=" + messageType +
               ", length=" + this.getLength() +
//               ", data=" + Arrays.toString(data) +
               '}';
    }
}
