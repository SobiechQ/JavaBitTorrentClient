package Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class HandshakeServiceImpl implements HandshakeService {
    private final static String PROTOCOL_NAME = "BitTorrent protocol";
    private final static byte[] PROTOCOL_NAME_BYTES = PROTOCOL_NAME.getBytes(java.nio.charset.StandardCharsets.UTF_8);

    @Override
    public HandshakeOutputProjection getHandshake(byte[] infoHash, byte[] peerId) {
        if (infoHash.length != 20 || peerId.length != 20)
            throw new IllegalArgumentException("Passed arrays have to be 20 bytes long.");

        final var handshake = new byte[68];
        handshake[0] = 19;

        System.arraycopy(PROTOCOL_NAME_BYTES, 0, handshake, 1, PROTOCOL_NAME_BYTES.length);

        Arrays.fill(handshake, 20, 28, (byte) 0);

        System.arraycopy(infoHash, 0, handshake, 28, infoHash.length);
        System.arraycopy(peerId, 0, handshake, 48, peerId.length);

        return new HandshakeOutputProjection(handshake, infoHash, peerId);
    }

    @Override
    public boolean verifyHandshake(@NonNull HandshakeInputProjection handshakeInput, byte @NonNull [] infoHash) {
        if (handshakeInput.handshake().length != 68 || handshakeInput.handshake()[0] != 19)
            return false;

        final var receivedProtocolName = new byte[19];
        System.arraycopy(handshakeInput.handshake(), 1, receivedProtocolName, 0, 19);

        if (!Arrays.equals(receivedProtocolName, PROTOCOL_NAME_BYTES))
            return false;

        final var receivedInfoHash = new byte[20];
        System.arraycopy(handshakeInput.handshake(), 28, receivedInfoHash, 0, 20);

        return Arrays.equals(receivedInfoHash, infoHash);
    }

}
