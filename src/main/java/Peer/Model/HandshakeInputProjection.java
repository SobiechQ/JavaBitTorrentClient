package Peer.Model;

import lombok.NonNull;

public record HandshakeInputProjection(byte @NonNull [] handshake) {
}
