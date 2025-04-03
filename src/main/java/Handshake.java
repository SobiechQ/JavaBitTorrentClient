import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">Bep 003 impl</a>
 */
public class Handshake {
    private final byte[] infoHash;
    private final byte[] peerId;

    public Handshake(byte[] infoHash, byte[] peerId) {
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
    public boolean get(Socket socket) throws IOException {
        socket.getOutputStream().write(Handshake.of(infoHash, peerId));
        final var received = socket.getInputStream().readNBytes(68);

        if (received[0] != 19)
            return false;

        final var receivedProtocolName = new byte[19];
        System.arraycopy(received, 1, receivedProtocolName, 0, 19);

        if (!Arrays.equals(receivedProtocolName, "BitTorrent protocol".getBytes()))
            return false;

        final var receivedInfoHash = new byte[20];
        System.arraycopy(received, 28, receivedInfoHash, 0, 20);

        if (!Arrays.equals(receivedInfoHash, this.infoHash))
            return false;

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
