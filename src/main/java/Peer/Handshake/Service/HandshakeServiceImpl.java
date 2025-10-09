package Peer.Handshake.Service;

import Peer.Model.HandshakeInputProjection;
import Peer.Model.HandshakeOutputProjection;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class HandshakeServiceImpl implements HandshakeService {

    @Override
    public HandshakeOutputProjection getHandshake(byte[] infoHash, byte[] peerId) {
        if (infoHash.length != 20 || peerId.length != 20)
            throw new IllegalArgumentException("Passed arrays have to be 20 bytes long.");

        final var handshake = new byte[68];
        handshake[0] = 19;

        final var protocolName = "BitTorrent protocol";
        for (int i = 0; i < protocolName.getBytes().length; i++)
            handshake[i + 1] = (byte) protocolName.charAt(i);

        System.arraycopy(infoHash, 0, handshake, 28, infoHash.length);
        System.arraycopy(peerId, 0, handshake, 48, infoHash.length);

        return new HandshakeOutputProjection(handshake, infoHash, peerId);
    }

    @Override
    public boolean verifyHandshake(@NonNull HandshakeInputProjection handshakeInput, @NonNull HandshakeOutputProjection handshakeOutput) {
        if (handshakeInput.handshake().length != 68 || handshakeInput.handshake()[0] != 19)
            return false;

        final var receivedProtocolName = new byte[19];
        System.arraycopy(handshakeInput.handshake(), 1, receivedProtocolName, 0, 19);

        if (!Arrays.equals(receivedProtocolName, "BitTorrent protocol".getBytes()))
            return false;

        final var receivedInfoHash = new byte[20];
        System.arraycopy(handshakeInput.handshake(), 28, receivedInfoHash, 0, 20);

        return Arrays.equals(receivedInfoHash, handshakeOutput.infoHash());
    }

}
