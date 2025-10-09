package Peer.Model;

import lombok.NonNull;

public record HandshakeOutputProjection(byte @NonNull [] handshake, byte @NonNull [] infoHash, byte @NonNull [] peerId) {

}
