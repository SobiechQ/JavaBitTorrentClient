package Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

@Service
public class HandshakeServiceImpl implements HandshakeService {
    private final static String PROTOCOL_NAME = "BitTorrent protocol";
    public final static byte[] PROTOCOL_NAME_BYTES = PROTOCOL_NAME.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    private final static byte[] PEER_ID = "00112233445566778899".getBytes();

    @Override
    public HandshakeOutputProjection getHandshake(@NonNull Torrent torrent) {
        final var handshake = new byte[68];
        handshake[0] = 19;

        System.arraycopy(PROTOCOL_NAME_BYTES, 0, handshake, 1, PROTOCOL_NAME_BYTES.length);

        Arrays.fill(handshake, 20, 28, (byte) 0);

        System.arraycopy(torrent.getInfoHash(), 0, handshake, 28, torrent.getInfoHash().length);
        System.arraycopy(PEER_ID, 0, handshake, 48, PEER_ID.length);

        return new HandshakeOutputProjection(handshake, torrent.getInfoHash(), PEER_ID);
    }

    @Override
    public boolean verifyHandshake(@NonNull Torrent torrent, @NonNull HandshakeInputProjection handshakeInput) {
        if (handshakeInput.handshake().length != 68 || handshakeInput.handshake()[0] != 19)
            return false;

        final var receivedProtocolName = new byte[19];
        System.arraycopy(handshakeInput.handshake(), 1, receivedProtocolName, 0, 19);

        if (!Arrays.equals(receivedProtocolName, PROTOCOL_NAME_BYTES))
            return false;

        final var receivedInfoHash = new byte[20];
        System.arraycopy(handshakeInput.handshake(), 28, receivedInfoHash, 0, 20);

        return Arrays.equals(receivedInfoHash, torrent.getInfoHash());
    }



}
