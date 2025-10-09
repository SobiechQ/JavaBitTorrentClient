package Peer.Handshake.Service;

import Peer.Model.HandshakeInputProjection;
import Peer.Model.HandshakeOutputProjection;

public interface HandshakeService {
    HandshakeOutputProjection getHandshake(byte[] infoHash, byte[] peerId);
    boolean verifyHandshake(HandshakeInputProjection handshakeInput, HandshakeOutputProjection handshakeOutput);
}
