package atodo.TCP;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">Bep 003 impl</a>
 */
public class Handshake {
    private final static Logger logger = LogManager.getLogger();
    private final byte[] infoHash;
    private final byte[] peerId;

    public Handshake(byte @NonNull [] infoHash, byte @NonNull [] peerId) {
        if (infoHash.length != 20 || peerId.length != 20)
            throw new IllegalArgumentException("Passed arrays have to be 20 bytes long.");
        this.infoHash = infoHash;
        this.peerId = peerId;
    }

    /**
     * Sends <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">a handshake</a> to provided socket, and waits for response.
     * Reads 68 bytes from inputStream - total length of handshake.
     * @param socket peer to send handshake to.
     * @return true - if handshake is successful and received handshake matches infoHash, false otherwise.
     * @throws IOException if socket connection malfunctions
     */
    public boolean get(@NonNull Socket socket) throws IOException {
        logger.info("Sending handshake to... {}", socket.getInetAddress());
        socket.getOutputStream().write(Handshake.of(infoHash, peerId));
        final var received = socket.getInputStream().readNBytes(68);

        if (received == null || received.length != 68 || received[0] != 19){
            logger.error("Handshake integrity check failed {}", received);
            return false;
        }

        final var receivedProtocolName = new byte[19];
        System.arraycopy(received, 1, receivedProtocolName, 0, 19);

        if (!Arrays.equals(receivedProtocolName, "BitTorrent protocol".getBytes())) {
            logger.error("Handshake integrity check failed, received protocol name does not match {}", receivedProtocolName);
            return false;
        }

        final var receivedInfoHash = new byte[20];
        System.arraycopy(received, 28, receivedInfoHash, 0, 20);

        final var handshakeCheck = Arrays.equals(receivedInfoHash, this.infoHash);

        if (!handshakeCheck) {
            logger.error("Handshake integrity check failed, received info hash: [{}], doest not match this.infoHash: [{}]", receivedInfoHash, this.infoHash);
            return false;
        }

        logger.info("Handshake properly established");
        return true;
    }

    private static byte[] of(byte[] infoHash, byte[] peerId) {
        if (infoHash.length != 20 || peerId.length != 20)
            throw new IllegalArgumentException("Passed arrays have to be 20 bytes long.");

        final var handshake = new byte[68];
        handshake[0] = 19;

        final var protocolName = "BitTorrent protocol";
        for (int i = 0; i < protocolName.getBytes().length; i++)
            handshake[i + 1] = (byte) protocolName.charAt(i);

        System.arraycopy(infoHash, 0, handshake, 28, infoHash.length);
        System.arraycopy(peerId, 0, handshake, 48, infoHash.length);

        return handshake;
    }




}
