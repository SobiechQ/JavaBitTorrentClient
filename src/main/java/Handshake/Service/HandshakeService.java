package Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import Model.DecodedBencode.Torrent;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface HandshakeService {
    String PROTOCOL_NAME = "BitTorrent protocol";
    byte[] PROTOCOL_NAME_BYTES = PROTOCOL_NAME.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    byte[] PEER_ID = "00112233445566778899".getBytes();

    HandshakeOutputProjection getHandshake(Torrent torrent);
    boolean verifyHandshake(Torrent torrent, HandshakeInputProjection handshakeInput);
}
