package Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;

public interface HandshakeService {
    HandshakeOutputProjection getHandshake(byte[] infoHash, byte[] peerId);
    boolean verifyHandshake(HandshakeInputProjection handshakeInput, byte[] handshakeOutput);
}
