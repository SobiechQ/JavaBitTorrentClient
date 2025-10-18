package Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import Model.DecodedBencode.Torrent;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface HandshakeService {
    HandshakeOutputProjection getHandshake(Torrent torrent);
    boolean verifyHandshake(Torrent torrent, HandshakeInputProjection handshakeInput);
}
